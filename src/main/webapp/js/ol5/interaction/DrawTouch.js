/*	Copyright (c) 2016 Jean-Marc VIGLINO, 
	released under the CeCILL-B license (French BSD license)
	(http://www.cecill.info/licences/Licence_CeCILL-B_V1-en.txt).
*/
/** Interaction DrawTouch :
 * @constructor
 * @extends {ol.interaction.CenterTouch}
 * @param {olx.interaction.DrawOptions} options
 *	- source {ol.source.Vector | undefined} Destination source for the drawn features.
 *	- type {ol.geom.GeometryType} Drawing type ('Point', 'LineString', 'Polygon') not ('MultiPoint', 'MultiLineString', 'MultiPolygon' or 'Circle'). Required.
 *	- tap {boolean} enable on tap, default true
 *	Inherited params
 *  - targetStyle {ol.style.Style|Array<ol.style.Style>} a style to draw the target point, default cross style
 *  - composite {string} composite operation : difference|multiply|xor|screen|overlay|darken|lighter|lighten|...
 */
ol.interaction.DrawTouch = function(options)
{	var options = options||{};
	var self = this;
	options.handleEvent = function(e)
	{	if (this.get("tap"))
		{	switch (e.type)
			{	case "singleclick":
					this.addPoint();
					break;
				case "dblclick":
					this.addPoint();
					this.finishDrawing();
					return false;
					break;
				default: break;
			}
		}
		return true;
	}
	ol.interaction.CenterTouch.call(this, options);
	this.typeGeom_ = options.type;
	this.source_ = options.source;
	this.set("tap", (options.tap!==false));
	// Style
	var white = [255, 255, 255, 1];
	var blue = [0, 153, 255, 1];
	var width = 3;
	var defaultStyle = [
		new ol.style.Style({
			stroke: new ol.style.Stroke({ color: white, width: width + 2 })
		}),
		new ol.style.Style({
			image: new ol.style.Circle({
				radius: width * 2,
				fill: new ol.style.Fill({ color: blue }),
				stroke: new ol.style.Stroke({ color: white, width: width / 2 })
			}),
			stroke: new ol.style.Stroke({ color: blue, width: width }),
			fill: new ol.style.Fill({
				color: [255, 255, 255, 0.5]
			})
		})
	];
	this.overlay_ = new ol.layer.Vector(
		{	source: new ol.source.Vector({useSpatialIndex: false }),
			style: defaultStyle
		});
	this.geom_ = [];
};
ol.inherits(ol.interaction.DrawTouch, ol.interaction.CenterTouch);
/**
 * Remove the interaction from its current map, if any,  and attach it to a new
 * map, if any. Pass `null` to just remove the interaction from the current map.
 * @param {ol.Map} map Map.
 * @api stable
 */
ol.interaction.DrawTouch.prototype.setMap = function(map) {
	if (this._listener.drawSketch) ol.Observable.unByKey(this._listener.drawSketch);
	this._listener.drawSketch = null;
	ol.interaction.CenterTouch.prototype.setMap.call (this, map);
	this.overlay_.setMap(map);
	if (this.getMap())
	{	this._listener.drawSketch = this.getMap().on("postcompose", this.drawSketchLink_.bind(this));
	}
};
/** Start drawing and add the sketch feature to the target layer. 
* The ol.interaction.Draw.EventType.DRAWSTART event is dispatched before inserting the feature.
*/
ol.interaction.DrawTouch.prototype.startDrawing = function()
{	this.geom_ = [];
	this.addPoint();
};
/** Get geometry type
* @return {ol.geom.GeometryType}
*/
ol.interaction.DrawTouch.prototype.getGeometryType = function()
{	return this.typeGeom_;
};
/** Start drawing and add the sketch feature to the target layer. 
* The ol.interaction.Draw.EventType.DRAWEND event is dispatched before inserting the feature.
*/
ol.interaction.DrawTouch.prototype.finishDrawing = function()
{	if (!this.getMap()) return;
	var f;
	switch (this.typeGeom_)
	{	case "LineString":
			if (this.geom_.length > 1) f = new ol.Feature(new ol.geom.LineString(this.geom_));
			break;
		case "Polygon":
			// Close polygon
			if (this.geom_[this.geom_.length-1] != this.geom_[0]) 
			{	this.geom_.push(this.geom_[0]);
			}
			// Valid ?
			if (this.geom_.length > 3) 
			{	f = new ol.Feature(new ol.geom.Polygon([ this.geom_ ]));
			}
			break;
		default: break;
	}
	if (f) this.source_.addFeature (f);
	// reset
	this.geom_ = [];
	this.drawSketch_();
}
/** Add a new Point to the drawing
*/
ol.interaction.DrawTouch.prototype.addPoint = function()
{	if (!this.getMap()) return;
	this.geom_.push(this.getPosition());
	switch (this.typeGeom_)
	{	case "Point": 
			var f = new ol.Feature( new ol.geom.Point (this.geom_.pop()));
			this.source_.addFeature(f);
			break;
		case "LineString":
		case "Polygon":
			this.drawSketch_();
			break;
		default: break;
	}
}
/** Remove last point of the feature currently being drawn.
*/
ol.interaction.DrawTouch.prototype.removeLastPoint = function()
{	if (!this.getMap()) return;
	this.geom_.pop();
	this.drawSketch_();
}
/** Draw sketch
* @private
*/
ol.interaction.DrawTouch.prototype.drawSketch_ = function()
{	if (!this.overlay_) return;
	this.overlay_.getSource().clear();
	if (this.geom_.length)
	{	var f;
		if (this.typeGeom_ == "Polygon") 
		{	f = new ol.Feature(new ol.geom.Polygon([this.geom_]));
			this.overlay_.getSource().addFeature(f);
		}
		var geom = new ol.geom.LineString(this.geom_);
		f = new ol.Feature(geom);
		this.overlay_.getSource().addFeature(f);
		f = new ol.Feature( new ol.geom.Point (this.geom_.slice(-1).pop()) );
		this.overlay_.getSource().addFeature(f);
	}
}
/** Draw contruction lines on postcompose
* @private
*/
ol.interaction.DrawTouch.prototype.drawSketchLink_ = function(e)
{	if (!this.getActive() || !this.getPosition()) return;
	var ctx = e.context;
	ctx.save();
		var p, pt = this.getMap().getPixelFromCoordinate(this.getPosition());
		var ratio = e.frameState.pixelRatio || 1;
		ctx.scale(ratio,ratio);
		ctx.strokeStyle = "rgba(0, 153, 255, 1)";
		ctx.lineWidth = 1;
		ctx.beginPath();
		ctx.arc (pt[0],pt[1], 5, 0, 2*Math.PI);
		ctx.stroke();
		if (this.geom_.length)
		{	p = this.getMap().getPixelFromCoordinate(this.geom_[this.geom_.length-1]);
			ctx.beginPath();
			ctx.moveTo(p[0],p[1]);
			ctx.lineTo(pt[0],pt[1]);
			if (this.typeGeom_ == "Polygon")
			{	p = this.getMap().getPixelFromCoordinate(this.geom_[0]);
				ctx.lineTo(p[0],p[1]);
			}
			ctx.stroke();
		}
	ctx.restore();
}
/**
 * Activate or deactivate the interaction.
 * @param {boolean} active Active.
 * @observable
 * @api
 */
ol.interaction.DrawTouch.prototype.setActive = function(b)
{	ol.interaction.CenterTouch.prototype.setActive.call (this, b);
	if (!b) this.geom_ = [];
	this.drawSketch_();
}
