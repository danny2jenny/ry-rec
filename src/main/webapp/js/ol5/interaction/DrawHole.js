/*	Copyright (c) 2017 Jean-Marc VIGLINO, 
	released under the CeCILL-B license (French BSD license)
	(http://www.cecill.info/licences/Licence_CeCILL-B_V1-en.txt).
*/
/** Interaction draw hole
 * @constructor
 * @extends {ol.interaction.Interaction}
 * @fires drawstart, drawend
 * @param {olx.interaction.DrawHoleOptions} options extend olx.interaction.DrawOptions
 * 	@param {Array<ol.layer.Vector> | undefined} options.layers A list of layers from which polygons should be selected. Alternatively, a filter function can be provided. default: all visible layers
 */
ol.interaction.DrawHole = function(options)
{	if (!options) options = {};
	var self = this;
	// Select interaction for the current feature
	this._select = new ol.interaction.Select();
	this._select.setActive(false);
	// Geometry function that test points inside the current
	var geometryFn, geomFn = options.geometryFunction;
	if (geomFn)
	{	geometryFn = function(c,g) 
		{ 	g = self._geometryFn (c, g);
			return geomFn (c,g);
		}
	}
	else
	{	geometryFn = function(c,g) { return self._geometryFn (c, g); }
	}
	// Create draw interaction
	options.type = "Polygon";
	options.geometryFunction = geometryFn;
	ol.interaction.Draw.call(this, options);
	// Layer filter function
	if (options.layers) 
	{	if (typeof (options.layers) === 'function') this.layers_ = options.layers;
		else if (options.layers.indexOf) 
		{	this.layers_ = function(l) 
			{ return (options.layers.indexOf(l) >= 0); 
			};
		}
	}
	// Start drawing if inside a feature
	this.on('drawstart', this._startDrawing.bind(this));
	// End drawing add the hole to the current Polygon
	this.on('drawend', this._finishDrawing.bind(this));
};
ol.inherits(ol.interaction.DrawHole, ol.interaction.Draw);
/**
 * Remove the interaction from its current map, if any,  and attach it to a new
 * map, if any. Pass `null` to just remove the interaction from the current map.
 * @param {ol.Map} map Map.
 * @api stable
 */
ol.interaction.DrawHole.prototype.setMap = function(map)
{	if (this.getMap()) this.getMap().removeInteraction(this._select);
	if (map) map.addInteraction(this._select);
	ol.interaction.Draw.prototype.setMap.call (this, map);
};
/**
 * Activate/deactivate the interaction
 * @param {boolean}
 * @api stable
 */
ol.interaction.DrawHole.prototype.setActive = function(b)
{	this._select.getFeatures().clear();
	ol.interaction.Draw.prototype.setActive.call (this, b);
};
/**
 * Remove last point of the feature currently being drawn 
 * (test if points to remove before).
 */
ol.interaction.DrawHole.prototype.removeLastPoint = function()
{	if (this._feature && this._feature.getGeometry().getCoordinates()[0].length>2) 
	{	ol.interaction.Draw.prototype.removeLastPoint.call(this);
	}
};
/** 
 * Get the current polygon to hole
 * @return {ol.Feature}
 */
ol.interaction.DrawHole.prototype.getPolygon = function()
{	return this._polygon;
	// return this._select.getFeatures().item(0).getGeometry();
};
/**
 * Get current feature to add a hole and start drawing
 * @param {ol.interaction.Draw.Event} e
 * @private
 */
ol.interaction.DrawHole.prototype._startDrawing = function(e)
{	var map = this.getMap();
	var layersFilter = this.layers_;
	this._feature = e.feature;
	var coord = e.feature.getGeometry().getCoordinates()[0][0];
	// Check object under the pointer
	var features = map.getFeaturesAtPixel(
		map.getPixelFromCoordinate(coord),
		{ 	layerFilter: layersFilter
		}
	);
	var current = null;
	if (features)
	{	var poly = features[0].getGeometry();
		if (poly.getType() === "Polygon"
			&& poly.intersectsCoordinate(coord)) {
				this._polygonIndex = false;
				this._polygon = poly;
				current = features[0];
			}
		else if (poly.getType() === "MultiPolygon"
			&& poly.intersectsCoordinate(coord)) {
				for (var i=0, p; p=poly.getPolygon(i); i++) {
					if (p.intersectsCoordinate(coord)) {
						this._polygonIndex = i;
						this._polygon = p;
						current = features[0];
						break;
					}
				}
			}
		else current = null;
	}
	console.log(this._polygonIndex)
	this._select.getFeatures().clear();
	if (!current)
	{	this.setActive(false);
		this.setActive(true);
	}
	else
	{	this._select.getFeatures().push(current);
	}
};
/**
 * Stop drawing and add the sketch feature to the target feature. 
 * @param {ol.interaction.Draw.Event} e
 * @private
 */
ol.interaction.DrawHole.prototype._finishDrawing = function(e)
{	// The feature is the hole
	e.hole = e.feature;
	// Get the current feature
	e.feature = this._select.getFeatures().item(0);
	// Create the hole
	var c = e.hole.getGeometry().getCoordinates()[0];
	if (c.length > 3) {
		if (this._polygonIndex!==false) {
			var geom = e.feature.getGeometry();
			var newGeom = new ol.geom.MultiPolygon();
			for (var i=0, pi; pi=geom.getPolygon(i); i++) {
				if (i===this._polygonIndex) {
					pi.appendLinearRing(new ol.geom.LinearRing(c));
					newGeom.appendPolygon(pi);
				}
				else newGeom.appendPolygon(pi);
			}
			e.feature.setGeometry(newGeom);
		} else {
			this.getPolygon().appendLinearRing(new ol.geom.LinearRing(c));
		}
	}
	// reset
	this._feature = null;
	this._select.getFeatures().clear();
};
/**
 * Function that is called when a geometry's coordinates are updated.
 * @param {Array<ol.coordinate>} coordinates
 * @param {ol.geom.Polygon} geometry
 * @return {ol.geom.Polygon}
 * @private
 */
ol.interaction.DrawHole.prototype._geometryFn = function(coordinates, geometry)
{	var coord = coordinates[0].pop();
	if (!this.getPolygon() || this.getPolygon().intersectsCoordinate(coord))
	{	this.lastOKCoord = [coord[0],coord[1]];
	}
	coordinates[0].push([this.lastOKCoord[0],this.lastOKCoord[1]]);
	if (geometry) 
	{	geometry.setCoordinates([coordinates[0].concat([coordinates[0][0]])]);
	} 
	else 
	{	geometry = new ol.geom.Polygon(coordinates);
	}
	return geometry;
};