/*	Copyright (c) 2017 Jean-Marc VIGLINO, 
	released under the CeCILL-B license (French BSD license)
	(http://www.cecill.info/licences/Licence_CeCILL-B_V1-en.txt).
*/
/** A source for hexagonal binning
* @constructor 
* @extends {ol.source.Vector}
* @param {} options ol.source.VectorOptions + ol.HexGridOptions
*	 @param {ol.source.Vector} options.source Source
*	 @param {Number} options.size size of the exagon in map units, default 80000
*	 @param {ol.coordinate} options.origin orgin of the grid, default [0,0]
*	 @param {pointy|flat} options.layout grid layout, default pointy
*	 @param {function|undefined} options.geometryFunction Function that takes an ol.Feature as argument and returns an ol.geom.Point as feature's center. 
*/
ol.source.HexBin = function(options) {
  options = options || {} ;
	// bind function for callback
	this._bind = { modify: this._onModifyFeature.bind(this) };
	ol.source.Vector.call (this, options);
	// The HexGrid
	this._hexgrid = new ol.HexGrid(options);
	this._bin = {};
	// Source and origin
	this._origin = options.source;
	// Geometry function to get a point
	this._geomFn = options.geometryFunction || ol.coordinate.getFeatureCenter || function(f) { return f.getGeometry().getFirstCoordinate(); };
	// Existing features
	this.reset();
	// Future features
	this._origin.on("addfeature", this._onAddFeature.bind(this));
	this._origin.on("removefeature", this._onRemoveFeature.bind(this));
};
ol.inherits (ol.source.HexBin, ol.source.Vector);
/**
 * On add feature
 * @param {ol.Event} e 
 * @private
 */
ol.source.HexBin.prototype._onAddFeature = function(e) {
  var f = e.feature || e.target;
  var h = this._hexgrid.coord2hex(this._geomFn(f));
	var id = h.toString();
	if (this._bin[id]) {
    this._bin[id].get('features').push(f);
	} else { 
    var ex = new ol.Feature(new ol.geom.Polygon([this._hexgrid.getHexagon(h)]));
		ex.set('features',[f]);
		ex.set('center', new ol.geom.Point(ol.extent.getCenter(ex.getGeometry().getExtent())));
		this._bin[id] = ex;
		this.addFeature(ex);
	}
	f.on("change", this._bind.modify);
};
/**
 * Get the hexagon of a feature
 * @param {ol.Feature} f 
 * @return {} the bin id, the index of the feature in the bin and a boolean if the feature has moved to an other bin
 */
ol.source.HexBin.prototype.getBin = function(f) {
  // Test if feature exists in the current hex
	var id = this._hexgrid.coord2hex(this._geomFn(f)).toString();
	if (this._bin[id]) {
    var index = this._bin[id].get('features').indexOf(f);
		if (index > -1) return { id:id, index:index };
	}
	// The feature has moved > check all bins
	for (id in this._bin) {
    var index = this._bin[id].get('features').indexOf(f);
		if (index > -1) return { id:id, index:index, moved:true };
	}
	return false;
};
/**
 * On remove feature
 * @param {ol.Event} e 
 * @param {*} bin 
 * @private
 */
ol.source.HexBin.prototype._onRemoveFeature = function(e, bin) {
  var f = e.feature || e.target;
  var b = bin || this.getBin(f);
	if (b) {
    var features = this._bin[b.id].get('features');
		features.splice(b.index, 1);
		if (!features.length) {
      this.removeFeature(this._bin[b.id]);
			delete this._bin[b.id];
		}
	} else {
    console.log("[ERROR:HexBin] remove feature feature doesn't exists anymore.");
	}
	f.un("change", this._bind.modify);
};
/**
 * A feature has been modified
 * @param {ol.Event} e 
 * @private
 */
ol.source.HexBin.prototype._onModifyFeature = function(e) {
  var bin = this.getBin(e.target);
	if (bin && bin.moved) {
    // remove from the bin
		this._onRemoveFeature(e, bin);
		// insert in the new bin
		this._onAddFeature(e);
	}	
	this.changed();
};
/** Clear all bins and generate a new one
 */
ol.source.HexBin.prototype.reset = function() {
  this._bin = {};
	this.clear();
	var features = this._origin.getFeatures();
	for (var i=0, f; f=features[i]; i++) {
    this._onAddFeature({ feature:f });
	};
};
/**
* Get the orginal source 
* @return {ol.source.Vector}
*/
ol.source.HexBin.prototype.getSource = function() {
  return this._origin;
};
