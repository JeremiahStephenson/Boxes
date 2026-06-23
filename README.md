# BitShapes (Formally Boxes/Drixels)
### Description
This is a simple project I started back in 2023 to fill in some time and to do some interesting things with Jetpack Compose.

I did release it in the Play Store but was eventually pulled by Google because I didn't make frequent updates. I was working a demanding job at the time and wasn't able to keep this project up to date. 

I have resumed work on this to get the 3 year old code up to date and to fix things up.

After getting the code up to date here are some ideas I would like to explore with this:
1. Kotlin/Compose Multiplatform.
    1. iOS app version perhaps
    2. Web version too
2. Content generation through AI. User can prompt the app to generate a project through a prompt.

Once I get the code looking better I will expand more here and add screenshots.

### Current known bugs
1. Pinch to zoom is broken on the main canvas. This worked as expected when I wrote this intially but something changed internally with Compose and now this is a bit janky and unreliable. Two finger pinch often doesn't initiate zoom and instead one of the two fingers draws on the canvas. 
