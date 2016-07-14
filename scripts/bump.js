// Modules
var fs      = require('fs');
var xml2js  = require('xml2js');
var partial = require('nexxa-partial');
var pkg     = require('../package.json');

// CONSTS
var FILE    = 'plugin.xml';
var VERSION = pkg.version;

// Bump!
fs.readFile(FILE, partial(parse, FILE, VERSION));

function parse(fileName, version, err, data) {
  if (err) {
    throw err;
  }

  return xml2js.parseString(data, partial(bump, fileName, version));
}

function bump(fileName, version, err, xml) {
  if (err) {
    throw err;
  }

  if (xml.plugin.$.version === version) {
    console.log('Version already bumped');

    return process.exit(0);
  }

  xml.plugin.$.version = version;

  var builder = new xml2js.Builder();
  var xmlStr  = builder.buildObject(xml);

  return fs.writeFile(FILE, xmlStr, partial(afterWrite, fileName, version));
}

function afterWrite(fileName, version, err) {
  if (err) {
    throw err;
  }

  return console.log(fileName + ' version bumped to ' + version);
}
