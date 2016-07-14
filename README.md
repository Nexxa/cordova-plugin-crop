# cordova-plugin-crop

Crop an image in a Cordova app


## Install

```
$ cordova plugin add --save cordova-plugin-crop
```

## Usage

```js
plugins.crop(
   function success () {},
   function fail () {},
   '/path/to/image',
   options
)
```

or, if you are running on an environment that supports Promises (Crosswalk, Android >= KitKat, iOS >= 8)

```js
plugins.crop
   .promise('/path/to/image', options)
   .then(function success (newPath) {})
   .catch(function fail (err) {})
```

## Options

 * *{Number}* **[quality=*100*]** - The resulting JPEG quality.
 * *{Number}* **[toSize=*1080*]** - Resize image to specified size

## Libraries used

 * iOS: [PEPhotoCropEditor](https://github.com/kishikawakatsumi/PEPhotoCropEditor)
 * Android: [android-crop](https://github.com/jdamcd/android-crop)

## Authors

- [Jeduan Cornejo](https://github.com/jeduan)
- [StefanoMagrassi](https://github.com/StefanoMagrassi)

## License

MIT
