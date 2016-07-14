/**
 * @file Cordova Crop Plugin javascript handlers
 * @author StefanoMagrassi <stefano.magrassi@gmail.com>, Jeduan Cornejo
 */

module.exports = crop;
module.exports.promise = cropAsync;

function crop(success, fail, image, options) {
  var spec = Object.create((options || {}));

  if (!spec.quality) {
    spec.quality = 100;
  }

  if (!spec.toSize) {
    spec.toSize  = 1080;
  }

  return cordova.exec(success, fail, 'CropPlugin', 'cropImage', [image, spec]);
}

function cropAsync(image, options) {
  return new Promise(function cropP(resolve, reject) {
    crop(resolve, reject, image, options);
  });
}
