import AppKit
import Foundation

guard CommandLine.arguments.count == 6 else {
    fatalError("Expected background, screenshot, output, and two caption lines")
}

let backgroundURL = URL(fileURLWithPath: CommandLine.arguments[1])
let screenshotURL = URL(fileURLWithPath: CommandLine.arguments[2])
let outputURL = URL(fileURLWithPath: CommandLine.arguments[3])
let headline = "\(CommandLine.arguments[4])\n\(CommandLine.arguments[5])" as NSString

guard
    let background = NSImage(contentsOf: backgroundURL),
    let screenshot = NSImage(contentsOf: screenshotURL)
else {
    fatalError("Unable to load source images")
}

let canvasSize = NSSize(width: 1080, height: 1920)
let canvas = NSImage(size: canvasSize)
canvas.lockFocus()
NSGraphicsContext.current?.imageInterpolation = .high

background.draw(
    in: NSRect(origin: .zero, size: canvasSize),
    from: NSRect(origin: .zero, size: background.size),
    operation: .copy,
    fraction: 1
)

let paragraph = NSMutableParagraphStyle()
paragraph.alignment = .center
paragraph.lineSpacing = 2

let textShadow = NSShadow()
textShadow.shadowColor = NSColor.black.withAlphaComponent(0.38)
textShadow.shadowBlurRadius = 12
textShadow.shadowOffset = NSSize(width: 0, height: -4)

headline.draw(
    in: NSRect(x: 90, y: 1625, width: 900, height: 205),
    withAttributes: [
        .font: NSFont.systemFont(ofSize: 72, weight: .bold),
        .foregroundColor: NSColor.white,
        .paragraphStyle: paragraph,
        .shadow: textShadow,
    ]
)

let outerRect = NSRect(x: 180, y: 46, width: 720, height: 1570)
let outerPath = NSBezierPath(roundedRect: outerRect, xRadius: 54, yRadius: 54)
NSGraphicsContext.saveGraphicsState()
let phoneShadow = NSShadow()
phoneShadow.shadowColor = NSColor.black.withAlphaComponent(0.52)
phoneShadow.shadowBlurRadius = 28
phoneShadow.shadowOffset = NSSize(width: 0, height: -12)
phoneShadow.set()
NSColor(calibratedRed: 0.025, green: 0.035, blue: 0.06, alpha: 1).setFill()
outerPath.fill()
NSGraphicsContext.restoreGraphicsState()

let screenRect = NSRect(x: 195, y: 61, width: 690, height: 1540)
let screenPath = NSBezierPath(roundedRect: screenRect, xRadius: 42, yRadius: 42)
NSGraphicsContext.saveGraphicsState()
screenPath.addClip()
screenshot.draw(
    in: screenRect,
    from: NSRect(origin: .zero, size: screenshot.size),
    operation: .sourceOver,
    fraction: 1
)
NSGraphicsContext.restoreGraphicsState()

NSColor.white.withAlphaComponent(0.28).setStroke()
outerPath.lineWidth = 2
outerPath.stroke()

canvas.unlockFocus()

guard
    let tiff = canvas.tiffRepresentation,
    let representation = NSBitmapImageRep(data: tiff),
    let png = representation.representation(using: .png, properties: [:])
else {
    fatalError("Unable to encode output")
}

try png.write(to: outputURL)
