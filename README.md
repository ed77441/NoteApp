# Note application

This project is inspired by [Microsoft OneNote](https://www.microsoft.com/zh-tw/microsoft-365/onenote/digital-note-taking-app), it's developed based on java swing-gui

## Prerequisite

This application should be run on **Java 9** or above

## External Library

* [FlatLaf](https://www.formdev.com/flatlaf) for look and feel
* [Thumbnailator](https://github.com/coobird/thumbnailator) for faster image resize and quality

## Description

* The outer component is `ContentPane`, all main component should be reside in here
* If there is multiple `ContentPane`, then you can drag the tab to move the order of it
* There is four main component can be added to this application, which are `TextPane`, `TablePane`, `ImagePane`, `CanvasPane`
* Each of them can be **dragged** around within application, **copy**, **cut**, **paste**, and **resized**
* Also resizing behavior will be slightly different depending on what component you're controlling

## Components Showcase

### Overview
![overview image](https://i.imgur.com/fzF8oga.png)

### TextPane
![textpane image](https://i.imgur.com/JZGSDtD.png)

#### Function
##### As you can see above, there is several text attribute that can be set on control panel

* Bold, italic and underline
* Foreground and background color
* Font size and family

### TablePane
![tablepane image1](https://i.imgur.com/AxF0PcT.png)
#### MultiSelect cells
![tablepane image2](https://i.imgur.com/R1vTwYY.png)

#### Function
##### `TablePane` is just a collection of `TextPane`, but the size of `TextPane` will be changed depending on the content of it

* The size of `TextPane` will be changed based on the percentage of max width of column
* You can multi-select cells to be able to set text attributes at once

### ImagePane
![imagepane image](https://i.imgur.com/PA5Np25.png)

#### Function

* Corner draggable points can be dragged to resize the image, it will retain current image's ratio
* North, south, east, west draggable points will break image's ratio, and new ratio will be recalculated

### CanvasPane
![canvaspane image1](https://i.imgur.com/lhzBgef.png)

#### MultiSelect line segments
![canvaspane image2](https://i.imgur.com/sGWPw2C.png)

#### Move and change attributes
![canvaspane image3](https://i.imgur.com/M7mgQF6.png)

#### Function
* Change line segments' thickness, color and position by multi-selecting or clicking
* You can also erase them using eraser

### ColorPane
![colorpane image1](https://i.imgur.com/UVzGioP.png)
![colorpane image2](https://i.imgur.com/uAtObHj.png)

#### Function
* select predefined colors 
* custom color can be generated by three slider, each represent R, G, B respetively 

### FileMenu
![filemenu image](https://i.imgur.com/6c3WiQS.png)

#### Function
* You can save file by clicking or using hotkey, the output file's extension should end with `.note`
* File size should be quite small, because we only save important attributes like size, position, text attribute, image data, line segment points, etc
