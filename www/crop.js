/*eslint-env node, es6*/
/*global cordova*/

module.exports = crop;
module.exports.promise = cropAsync;

function crop(success, fail, image, options) {
  var spec = Object.create((options || {}));

  spec.quality = spec.quality || 100;

  return cordova.exec(success, fail, 'CropPlugin', 'cropImage', [image, spec]);
}

function cropAsync(image, options) {
  return new Promise(function (resolve, reject) {
    crop(resolve, reject, image, options);
  });
}
