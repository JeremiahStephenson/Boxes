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

let scale = screenshot.size.width / 1200
let canvasSize = NSSize(width: 1200 * scale, height: 1920 * scale)
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
paragraph.lineSpacing = 2 * scale

let textShadow = NSShadow()
textShadow.shadowColor = NSColor.black.withAlphaComponent(0.38)
textShadow.shadowBlurRadius = 12 * scale
textShadow.shadowOffset = NSSize(width: 0, height: -4 * scale)

headline.draw(
    in: NSRect(x: 100 * scale, y: 1625 * scale, width: 1000 * scale, height: 205 * scale),
    withAttributes: [
        .font: NSFont.systemFont(ofSize: 72 * scale, weight: .bold),
        .foregroundColor: NSColor.white,
        .paragraphStyle: paragraph,
        .shadow: textShadow,
    ]
)

let outerRect = NSRect(x: 120 * scale, y: 40 * scale, width: 960 * scale, height: 1508 * scale)
let outerPath = NSBezierPath(roundedRect: outerRect, xRadius: 52 * scale, yRadius: 52 * scale)
NSGraphicsContext.saveGraphicsState()
let tabletShadow = NSShadow()
tabletShadow.shadowColor = NSColor.black.withAlphaComponent(0.52)
tabletShadow.shadowBlurRadius = 28 * scale
tabletShadow.shadowOffset = NSSize(width: 0, height: -12 * scale)
tabletShadow.set()
NSColor(calibratedRed: 0.025, green: 0.035, blue: 0.06, alpha: 1).setFill()
outerPath.fill()
NSGraphicsContext.restoreGraphicsState()

let screenRect = NSRect(x: 138 * scale, y: 55 * scale, width: 924 * scale, height: 1478.4 * scale)
let screenPath = NSBezierPath(roundedRect: screenRect, xRadius: 40 * scale, yRadius: 40 * scale)
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
outerPath.lineWidth = 2 * scale
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
