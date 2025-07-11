# Room-Budget (Beta)
## Alias Integration4

Room Budget is a Kotlin based Android application that performs operations like sending, receiving, and manipulating data from Google Spreadsheets using Google App Script. The app was developed using Android Studio and facilitates efficient budget management for users. And is developed by `Nithish Gajula`

https://github.com/ifyun/markdown-it-android - need to look and update logs 


### Developer Shortcuts :

To preview `Readme.md` file in VSCode -> `Ctrl + Shift + V`
To format the json document in VSCode -> `Ctrl + K` then `Ctrl + F`
To align the code in Android Studio -> `Ctrl + Alt + L`
To Minimize and Maximize the Functions in Android Studio -> `Ctrl + Shift + -` and `Ctrl + Shift + +`

### Wireless Debugging :

- Connect Mobile to 'ICPS 3FL 5GHz' wifi
- make sure adb installed in desktop using 'adb version' command
- Developer Options -> Wireless Debugging -> Enable -> Pair device with pairing code -> Note the IP, Port and Pairing Code (changes everytime)
- type command in desktop in *sudo* : adb pair <IP>:<Port>
- It asks for pairing code, Enter pairing code
- If success, then type : adb connect <IP>:<Port>
- adb devices, you can see the connected devices
- android studio automatically detects it.

Arial Black
Dialog
Fira Code
Fira Code Medium
HP Simplified
Inter
Inter Semi Bold
Microsoft Sans Serif
Nirmal UI
SansSerif
Verdana

ffc34d

Below is example markdown content


# Heading 1
## Heading 2
### Heading 3
#### Heading 4
##### Heading 5
###### Heading 6

---

## Paragraphs
This is a paragraph in Markdown. To start a new paragraph, leave a blank line between lines of text.

This is another paragraph.

## Line Breaks
Add two spaces at the end of a line  
to create a line break.

## Bold and Italics
- **Bold text** using `**bold text**`
- *Italic text* using `*italic text*`
- ***Bold and Italic text*** using `***bold and italic text***`

## Blockquotes
> This is a blockquote.
> 
> It can span multiple lines.

> > Nested blockquotes are also possible.

---

## Lists

### Unordered List
- Item 1
  - Sub-item 1.1
  - Sub-item 1.2
- Item 2

### Ordered List
1. First item
2. Second item
   1. Sub-item 2.1
   2. Sub-item 2.2

---

## Code

### Inline Code
Use inline code with backticks, like this: `inline code`.

### Code Block

---

## Horizontal Rule
Create a horizontal rule by using three or more `-`, `*`, or `_`:

---

## Links
This is an [inline link](https://www.example.com).

This is a [reference-style link][example-link].

[example-link]: https://www.example.com

---

## Images
Inline image: ![Markdown Logo](https://markdown-here.com/img/icon256.png)

Reference-style image: ![Markdown Logo][logo]

[logo]: https://markdown-here.com/img/icon256.png

---

## Tables

| Header 1    | Header 2    |
| ----------- | ----------- |
| Row 1, Col 1| Row 1, Col 2|
| Row 2, Col 1| Row 2, Col 2|

---

## Footnotes
Here's a sentence with a footnote.[^1]

[^1]: This is the footnote text.

---

## Strikethrough
~~Strikethrough text~~ using `~~strikethrough~~`.

---

## Task Lists
- [x] Task 1 (completed)
- [ ] Task 2 (not completed)

---

## HTML Elements
You can also include raw HTML elements.  
For example: `<br>` adds a line break.
