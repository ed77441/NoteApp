# Note application

This project is inspired by `Microsoft OneNote`, and only for practicing programming skill purpose only

## Prerequisite

This application should be run on **Java 9** or above

## External Library

* `FlatLaf` for look and feel
* `Thumbnailator` for faster image resize and quality

## Description

* The outer component is `ContentPane`, all main component should be reside in here
* If there is multiple `ContentPane`, then you can drag the tab to move the order of it
* There is four main component can be added to this application, which are `TextPane`, `TablePane`, `ImagePane`, `CanvasPane`
* Each of them can be **dragged** around within application, **copy**, **cut**, **paste**, and **resized**
* Also resized behavior will be slightly different depending on what component you're controlled on

## Components Showcase

### Overview
![overview image](https://i.imgur.com/fzF8oga.png)

### TextPane
![textpane image](https://i.imgur.com/JZGSDtD.png)

#### Function
##### As you seen above, there is several text attribute can be set on control panel

1. Bold, italic and underline
2. Foreground and background color
3. Font size and family

### TablePane
![tablepane image1](https://i.imgur.com/AxF0PcT.png)
#### MultiSelect cells
![tablepane image2](https://i.imgur.com/R1vTwYY.png)

#### Function
##### `TablePane` is just a collection of `TextPane`, but the size of `TextPane` will be changed depending on the content of it

1. The size of TextPane will be changed based on the percentage of max width of column
2. You can multi-select cells to be able to set text attributes at once

### ImagePane
![imagepane image](https://i.imgur.com/PA5Np25.png)

#### Function

1. Corner draggable point can be dragged to resize the image, it will contains current image ratio
2. North, south, east, west draggable points will break image ratio and create new one

### CanvasPane
![canvaspane image1](https://i.imgur.com/lhzBgef.png)

#### MultiSelect line segments
![canvaspane image2](https://i.imgur.com/sGWPw2C.png)

#### Move and change attributes
![canvaspane image3](https://i.imgur.com/M7mgQF6.png)

#### Function
1. Change line segments thickness and color by selecting some of it, or even move it
2. You can also erase some of them

### ColorPane
![colorpane image1](https://i.imgur.com/UVzGioP.png)
![colorpane image2](https://i.imgur.com/uAtObHj.png)

#### Function
1. select predefined colors 
2. custom color can be generated by three slider, each represent R, G, B respetively 

### FileMenu
![filemenu image](https://i.imgur.com/6c3WiQS.png)

#### Function
1. You can save file by click or shortcut, the output file's extension end with `.note`
2. File size should quite small, because we only save important attribute like size, position, text attribute, image data, line segment points, etc
