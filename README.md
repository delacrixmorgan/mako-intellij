# 🐟 Mako — Android Drawable Importer Plugin for IntelliJ / Android Studio

[![JetBrains Plugin](https://img.shields.io/jetbrains/plugin/v/27961)](https://plugins.jetbrains.com/plugin/27961)

> Still manually scaling images for Android drawable folders? Come on now. Mako is here to help.

![screenshot_overview](screenshots/0_overview.gif)

## ✏️ Article

- [The Android Plugin I Couldn’t Live Without — So I Rebuilt It from Scratch](https://medium.com/bugless/the-android-plugin-i-couldnt-live-without-so-i-rebuilt-it-from-scratch-73adda89ddd3)

## ✨ What Is This?

Mako is a no-nonsense, drag-and-drop image importer plugin for Android Studio and IntelliJ IDEA.  
It lets you import your `xxxhdpi` images and automatically scales them into the proper
Android [density-specific drawable directories](https://developer.android.com/training/multiscreen/screendensities)
like:

- `drawable-mdpi/`
- `drawable-hdpi/`
- `drawable-xhdpi/`
- `drawable-xxhdpi/`
- `drawable-xxxhdpi/`

Need a `drawable-night-xxhdpi` or `drawable-de-xxhdpi` instead? No problem — just enter a modifier.  
Drag in your `xxxhdpi` image(s) — and you’re done.

## 🧰 Features

- ✅ Drag and drop to import images
- ✅ Automatically scales `xxxhdpi` images down to all required densities
- ✅ Supports custom folder modifiers like `drawable-night-xxhdpi` or `drawable-zh-xxhdpi`
- ✅ Supports common formats: **PNG**, **JPG**, **JPEG**, and yes... **WebP!**
- ✅ Choose between **three resize algorithms** (Native, Thumbnailator, Imgscalr) for optimal image quality
- ✅ Multi-image batch import with **preview**
- ✅ Designed with simplicity for your **everyday Android developers**
- ✅ Open source and **friendly** ❤️
- ✅ Localised in:
    - English (`en`)
    - Chinese Simplified (`zh`)
    - Japanese (`jp`)
    - Spanish (`es`)
    - Portuguese (Brazil) (`pt-BR`)

## 📸 Screenshots

![screenshot_menu](screenshots/1_menu.png)
![screenshot_dialog](screenshots/2_dialog.png)
![screenshot_drawable](screenshots/3_drawable.png)

## 🚀 Installation

**Option 1 - JetBrains Plugin Marketplace**

[JetBrains Plugin Marketplace - Mako Android Drawable Importer](https://plugins.jetbrains.com/plugin/27961-mako-android-drawable-importer)

**Option 2 - Manual Installation via ZIP**

[Click here](https://github.com/delacrixmorgan/mako-intellij/release/download/mako-1.0.1.zip) to download the ZIP.

## 🧪 Supported Image Formats

- PNG
- JPG / JPEG
- WebP

> ❗ We wanted to support JPEG XL, but it’s still not widely supported on the JVM side (yet!). Keep an eye out though.

## 🎨 Resize Algorithms

Mako offers three different resize algorithms, each with its own approach to image quality and performance. You can
select your preferred algorithm in the import dialog.

<img width="684" height="576" alt="image" src="https://github.com/user-attachments/assets/17d40f15-c927-406c-ad3a-2ca6f972ddbc" />

### Algorithm Comparison

**1. Native (Graphics2D)**

- Single-pass resize
- Interpolation method: Bilinear
- Fast and dependency-free

**2. Thumbnailator**

- Uses progressive downscaling (multiple passes when downscaling significantly)
- Interpolation method: Bicubic
- Good balance of speed and quality

**3. Imgscalr**

- Uses multiple passes to minimize quality loss
- Applies anti-aliasing and edge preservation
- Interpolation method: Bicubic
- Best quality, slightly slower

### Visual Comparison

| Native                                                                                                                          |                                                             Thumbnailator                                                             | Imgscalr                                                                                                                              |
|---------------------------------------------------------------------------------------------------------------------------------|:-------------------------------------------------------------------------------------------------------------------------------------:|---------------------------------------------------------------------------------------------------------------------------------------|
| <img width="63" height="79" alt="icon" src="https://github.com/user-attachments/assets/d46a445b-6969-4929-8089-61c8d1b7c676" /> | <img width="62" height="79" alt="icon_thumb" src="https://github.com/user-attachments/assets/b961c076-eb4a-4e57-b708-d455dc7a9e84" /> | <img width="62" height="79" alt="icon_scalr" src="https://github.com/user-attachments/assets/4b021489-153c-4ddc-8596-1fcdfeb23b45" /> |

## 🛠 How It Works

1) Open your Android project and right-click on your `res` package, it will have `Mako Android Drawable Importer` just
   underneath `Paste`.
2) You provide a high-resolution `xxxhdpi` image.
3) Select your preferred resize algorithm (Native, Thumbnailator, or Imgscalr).
4) Mako scales the image down to the lower density buckets using your selected algorithm.
5) It writes the resized images into proper drawable-* folders using your output directory and optional modifier.
6) Done. No manual resizing, no need to learn Photoshop and most of all no more crying.

## 🌍 Localisation

Want to help translate? PRs welcome!

Currently supported:

| Language       | Code  |
|----------------|-------|
| English        | en    |
| 中文 (简体)        | zh    |
| 日本語            | jp    |
| Español        | es    |
| Português (BR) | pt-BR |

## 🤝 Contributing

Thanks for your interest in contributing! 🎉
We welcome code contributions, bug reports, feature requests, documentation improvements, and ideas.

You can help in many ways:
- 🐛 Report bugs (or help fix them!)
- ✨ Suggest new features or improvements
- 📝 Improve documentation
- 🔧 Submit pull requests
- 💬 Ask questions or discuss ideas

## 📄 License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE.md](LICENSE.md) file for details.