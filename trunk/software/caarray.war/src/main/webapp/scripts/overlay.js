/**
 * @author <a href="mailto:tomasz.szymczyszyn@gmail.com">Tomasz Szymczyszyn</a>
 * @version 0.1.0
 * @fileOverview Simple utility allowing to show content inside an overlay window.
 *
 * <p>
 * Features:
 *
 * <ul>
 *   <li>transparency support</li>
 *   <li>animation support (limited customization)</li>
 *   <li>modal / non-modal</li>
 *   <li>vertical positioning: top, center, bottom</li>
 *   <li>custom event hooks</li>
 *   <li>works with works with Firefox 2/3, Opera 9, IE 6/7/8, Safari 3</li>
 *   <li>open source (licensed under New BSD License)</li>
 * </ul>
 * </p>
 *
 * <p>Basic example of usage:
 *
 * <code><pre>
 *   var overlay = new Overlay().show(content);
 *   overlay.hide();
 * </pre></code>
 *
 * Where <code>content</code> is a HTML string (i.e. "<code>&lt;p class="msg"&gt;Foo!&lt;/p&gt;</code>") or a DOM element.<br />
 * </p>
 * 
 * <p>For more examples see the <a href='http://code.google.com/p/prototype-overlay'>project page</a> and doc comments below.</p>
 *
 * <p>
 * Copyright (c) 2008, Tomasz Szymczyszyn <tomasz.szymczyszyn@gmail.com><br />
 * All rights reserved.<br />
 * <br />
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:<br />
 *
 * <ul>
 *   <li>Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.</li>
 *   <li>Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.</li>
 *   <li>Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.</li>
 * </ul>
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * </p>
 */

/**
 * @class
 */
var Overlay = Class.create(/** @lends Overlay# */ {
  /** @private */
  current_options : null,

  /**
   * DOM element which holds contents of the overlay.
   *
   * @private
   */
  holder : null,

  /**
   * DOM element which serves as the background.
   *
   * @private
   */
  background : null,

  /** @private */
  holder_shown : false,

  /** @private */
  bckg_shown : false,

  /** @private */
  hiding_in_progress : false,

  /** @private */
  hide_timer : null,

  /** @private */
  click_event_handler : null,

  /** @private */
  window_events_handler : null,

  /**
   * Reference to parent of shown element (not used when displaying HTML fragment)
   *
   * @private
   */
  content_parent_element : null,

  /**
   * Reference to shown element (not used when displaying HTML fragment)
   *
   * @private
   */
  content_element : null,

  /**
   * Creates the holder and background elements and performs
   * other initiation tasks.
   *
   * @private
   */
  init : function() {
    if (this.holder)
      return;

    this.holder = this.init_holder();
    this.background = this.init_background();

    Element.extend(document);
    Element.extend(document.documentElement);
  },

  /**
   * Creates holder element and attaches it to the DOM.
   *
   * @private
   */
  init_holder : function() {
    var holder = document.createElement('div');
    Element.extend(holder);
    
    holder.identify();

    holder.style.position = 'absolute';

    var opacity = this.option('opacity');
    if (opacity)
      holder.setOpacity(opacity);

    holder.hide();

    document.body.appendChild(holder);

    return holder;
  },

  /**
   * Positions the holder element.
   *
   * @private
   */
  position_holder : function(force_reposition) {
    var holder = this.holder;

    var holder_dims = holder.getDimensions();

    // NOTE: hack for Opera 9.5 (needs testing with other versions)
    //       needed by Prototype 1.6.2 (may be fixed later)
    if (Prototype.Browser.Opera) {
      var viewport_dims = {
        width : window.innerWidth,
        height : window.innerHeight
      };
    }
    else
      var viewport_dims = document.viewport.getDimensions();

    var viewport_scroll_offsets = document.viewport.getScrollOffsets();
 
    var position = this.option('position');

    holder.style.left = (viewport_dims.width - holder_dims.width) / 2 + viewport_scroll_offsets.left + 'px';

    if (this.option('auto_reposition') || force_reposition) {
      if (position == 'center')
        holder.style.top = (viewport_dims.height - holder_dims.height) / 2 + viewport_scroll_offsets.top + 'px';
      else if (position == 'top')
        holder.style.top = viewport_scroll_offsets.top + 'px';
      else if (position == 'bottom')
        holder.style.top = viewport_dims.height - holder_dims.height + viewport_scroll_offsets.top + 'px';
      else
        throw "Unsupported position: " + position;
    }
    else {
      if (position == 'center')
        holder.style.top = (viewport_dims.height - holder_dims.height) / 2 + 'px';
      else if (position == 'top')
        holder.style.top = '0px';
      else if (position == 'bottom')
        holder.style.top = viewport_dims.height - holder_dims.height + 'px';
      else
        throw "Unsupported position: " + position;
    }    
  },

  /**
   * Creates background element and attaches it to the DOM.
   *
   * @private
   */
  init_background : function() {
    var background = document.createElement('div');
    Element.extend(background);

    background.style.position = 'absolute';
    background.style.left = 0;
    background.style.top = 0;
    background.style.backgroundColor = '#000';

    var bckg_opacity = this.option('bckg_opacity');
    if (bckg_opacity)
      background.setOpacity(bckg_opacity);

    background.hide();

    document.body.appendChild(background);

    return background;
  },

  /** 
   * Positions and resized the background so that it fits the viewport.
   *
   * @private
   */
  position_and_size_background : function() {
    var document_dims = document.documentElement.getDimensions();
    var document_offsets = document.documentElement.cumulativeOffset();
    var viewport_dims = document.viewport.getDimensions();
    var viewport_scroll_offsets = document.viewport.getScrollOffsets();
    
    var width = Math.max(document_dims.width + document_offsets.left, viewport_dims.width + viewport_scroll_offsets.left);
    var height = Math.max(document_dims.height + document_offsets.top, viewport_dims.height + viewport_scroll_offsets.top);

    this.background.style.width = width + 'px'; 
    this.background.style.height = height + 'px'; 
    this.background.style.left = 0;
    this.background.style.top = 0;
  },

  /** @private */
  update : function(content) {
    this.holder.className = this.option('cls');
    this.holder.style.zIndex = this.option('z-index');
    this.background.style.zIndex = this.option('z-index') - 1;

    this.restore_content_element();
    
    if (typeof content == typeof "") {
      this.holder.update(content);
    }
    else {
      this.content_element = content;
      this.content_parent_element = content.parentNode;

      this.holder.update(null);
      this.holder.appendChild(content);
    }

    this.position_holder(true);

    if (this.option('modal')) {
      this.background.className = this.option('bckg_cls');
      this.position_and_size_background();
    };
  },

  /**
   * Shows content in the overlay.
   *
   * @param content (x)html text or a DOM element
   * @param options hash of options allowing for customisation of shown overlay (optional)
   * @public
   */
  show : function(content, options) {
    Overlay.instances.push(this);

    this.current_options = options;

    this.clear_hide_timer();

    $$('select').each(function(e) { e.setStyle({ visibility : 'hidden' }); });

    this.init();
    this.update(content);
    this.start_handling_window_events();

    $$('#' + this.holder.id + ' select').each(function(e) { e.setStyle({ visibility : 'visible' }); });

    var beforeshow = this.option('beforeshow');
    if (beforeshow)
      beforeshow(this);

    if (this.option('animate')) {
      var animation_duration = this.option('animation_duration');
      var holder_animation_options = this.show_animation_options(false);

      if (this.holder_shown)
        new Effect.Opacity(this.holder, holder_animation_options);
      else
        this.holder.appear(holder_animation_options);

      if (this.option('modal')) {
        var bckg_animation_options = this.show_animation_options(true);
        if (this.bckg_shown)
          new Effect.Opacity(this.background, bckg_animation_options);
        else
          this.background.appear(bckg_animation_options);
      }
      else 
        this.background.fade(this.hide_animation_options(true));

      var that = this;
      setTimeout(function() { that.complete_show(); }, animation_duration);
    }
    else {
      this.holder.show();
      this.option('modal') ? this.background.show() : this.background.hide();

      this.complete_show();
    };

    this.holder_shown = true;
    this.bckg_shown = Boolean(this.option('modal'));
  },

  /** 
   * Fires aftershow callback. Setups window event handling and the timer
   * used to hide overlay after given time (if needed). Called when animation 
   * stops and overlay is completely shown.
   *
   * @private
   */
  complete_show : function() {
    var aftershow = this.option('aftershow');
    if (aftershow)
      aftershow(this);

    this.set_hide_timer();
    this.start_handling_click_events();
  },

  /** 
   * Hides the overlay (if overlay is animated - starts the animation).
   *
   * @public 
   */
  hide : function() {
    if (this.hiding_in_progress)
      return;

    this.hiding_in_progress = true;

    this.clear_hide_timer();
    this.stop_handling_click_events();

    if (this.option('animate')) {
      var animation_duration = this.option('animation_duration');

      this.holder.fade(this.hide_animation_options(false));
      if (this.option('modal'))
        this.background.fade(this.hide_animation_options(true));

      var that = this;
      setTimeout(function() { that.complete_hide(); }, animation_duration);
    }
    else {
      this.holder.hide();
      this.background.hide();

      this.complete_hide();
    };

    this.holder_shown = false;
    this.bckg_shown = false;
  },

  /**
   * Called when animation stops and overlay is completely hidden.
   *
   * @private
   */
  complete_hide : function() {
    Overlay.instances = Overlay.instances.without(this);

    this.stop_handling_window_events();

    if (Overlay.instances.length == 0)
      $$('select').each(function(e) { e.setStyle({ visibility : 'visible' }); });
    else
      $$('#' + Overlay.instances.last().holder.id + ' select').each(function(e) { e.setStyle({ visibility : 'visible' }); });

    this.clear_hide_timer();
    this.restore_content_element();

    var afterhide = this.option('afterhide');
    if (afterhide)
      afterhide(this);

    this.hiding_in_progress = false;
  },

  /** @private */
  start_handling_click_events : function() {
    if (! this.option('click_hide')) {
      this.stop_handling_click_events();
      return;
    }

    if (! this.click_event_handler) {                                
      var that = this;

      /** @ignore */
      this.click_event_handler = function() { that.hide(); };
    };

    Event.observe(this.holder, 'click', this.click_event_handler);
    Event.observe(this.background, 'click', this.click_event_handler);
  },

  /** @private */
  stop_handling_click_events : function() {
    Event.stopObserving(this.holder, 'click', this.click_event_handler);
    Event.stopObserving(this.background, 'click', this.click_event_handler);
  },                                

  /** @private */
  start_handling_window_events : function() {
    var that = this;
    if (this.option('modal'))
      /** @ignore */
      this.window_events_handler = function() { that.position_holder(); that.position_and_size_background(); };
    else
      /** @ignore */
      this.window_events_handler = function() { that.position_holder(); };

    Event.observe(window, 'resize', this.window_events_handler);
    Event.observe(window, 'scroll', this.window_events_handler);
  },

  /** @private */
  stop_handling_window_events : function() {
    Event.stopObserving(window, 'resize', this.window_events_handler);
    Event.stopObserving(window, 'scroll', this.window_events_handler);
  },

  /**
   * Set timer that hides the overlay if duration is provided.
   *
   * @private
   */
  set_hide_timer : function() {
    var auto_hide = this.option('auto_hide');
    var duration = this.option('duration');

    if (auto_hide && duration) {
      var that = this;
      this.hide_timer = setTimeout(function() { that.hide(); }, duration);
    }
  },

  /**
   * Cancel timer that hides the overlay.
   *
   * @private
   */
  clear_hide_timer : function() {
    if (this.hide_timer) {
      clearTimeout(this.hide_timer);
      this.hide_timer = null;
    };
  },

  /**
   * Return value of current option if set, otheriwse return default value or null in case
   * when no default is provided.
   *
   * @private
   * @param {String} name name of the option
   * @returns value of the option or default value or null 
   */
  option : function(name) {
    if (this.current_options && this.current_options[name] !== undefined)
      return this.current_options[name];

    if (Overlay.default_options[name] !== undefined)
      return Overlay.default_options[name];

    return null;
  },

  /**
   * Returns object containing parameters for the animation
   * that shows the window or background.
   *
   * @private
   * @param {Boolean} [for_background=false]
   * @returns {Object} animation parameters
   */
  show_animation_options : function(for_background) {
    return { duration : this.option('animation_duration') / 1000,
             to : this.option(for_background ? 'bckg_opacity' : 'opacity') };
  },

  /**
   * Returns object containing parameters for the animation
   * that hides the window or background.
   *
   * @private
   * @param {Boolean} [for_background=false]
   * @returns {Object} animation parameters
   */
  hide_animation_options : function(for_background) {
    return { duration : this.option('animation_duration') / 1000,
             from : this.option(for_background ? 'bckg_opacity' : 'opacity') };
  },

  /**
   * Reattach content (if content is a DOM node not HTML fragment) to
   * its original parent (if such exists).
   *
   * @private
   */
  restore_content_element : function() {
    if (this.content_element && this.content_parent_element)
      this.content_parent_element.appendChild(this.content_element);

    this.content_element = null;
    this.content_parent_element = null;
  }
});

Object.extend(Overlay, /** @lends Overlay */ {
  /**
   * Stack of currently shown overlay windows.
   *
   * @private
   */
  instances : [],

  /** @private */
  valid_options : ['cls',                               //class of the content holder
                   'bckg_cls',                          //class of the background
                   'opacity',                           //opacity of the holder in [0, 1] scale
                   'bckg_opacity',                      //opacity of the backgorund in [0, 1] scale
                   'position',                          //position of the content holder
                   'z-index',                           //z-index of the content holder
                   'click_hide',                        //toggle hiding on click
                   'auto_hide',                         //toggle hiding after specified time
                   'auto_reposition',                   //toggle automatic vertical repositioning when scrolling viewport
                   'duration',                          //message visiblity time in miliseconds
                   'modal',
                   'animate',                           //toggle predefined animations
                   'animation_duration',                //show / hide effect duration in miliseconds
                   'beforeshow',                        //event handler fired immediatelly before showing holder
                   'aftershow',                         //event handler fired immediatelly after showing holder
                   'afterhide'],                        //event handler fired immediatelly after hiding holder  

  /** @private */
  default_options : { 'z-index' : 9999,
                      'position' : 'center',
                      'click_hide' : true,
                      'auto_hide' : true,
                      'auto_reposition' : true,
                      'duration' : 5000,
                      'animate' : true,
                      'animation_duration' :  500,
                      'opacity' : 0.9,
                      'bckg_opacity' : 0.6 },

  /**
   * Sets new default options.
   *
   * Available options:
   *
   * <ul>
   *   <li><code>cls</code> - <code>class</code> of the content holder element</li>
   *   <li><code>bckg_cls</code> - <code>class</code> of the background element</li>
   *   <li><code>opacity</code> - opacity of the holder (in range of 0 to 1, default: 0.9)</li>
   *   <li><code>bckg_opacity</code> - opacity of the background (range of 0 to 1, default: 0.6)</li>
   *   <li><code>z-index</code> - z-index of the content holder (default: 9999, z-index of the background is lower by one)</li>
   *   <li><code>position</code> - one of 'top', 'center' or 'bottom' (default: 'center')</li>
   *   <li><code>click_hide</code> - toggle hiding on click (default: true)</li>
   *   <li><code>auto_hide</code> - toggle hiding after specified time (default: true)</li>
   *   <li><code>auto_reposition</code> - toggle automatic vertical repositioning (default: true)</li>
   *   <li><code>duration</code> - message visiblity time in miliseconds (default: 5000, matters only with <code>auto_hide</code> set to true)</li>
   *   <li><code>modal</code> - modal / non-modal (default: false)</li> 
   *   <li><code>animate</code> - toggle animations (default: true)</li>
   *   <li><code>animation_duration</code> - show / hide effect duration in miliseconds (default: 500)</li>
   *   <li><code>beforeshow</code> - no-arg function fired immediatelly before showing holder</li>
   *   <li><code>aftershow</code> - no-arg function fired immediatelly after showing holder</li>
   *   <li><code>afterhide</code> - no-arg function fired immediatelly after hiding holder</li>
   * </ul>
   *
   * @param new_default_options hash of new default options
   * @public
   */
  defaults : function(new_default_options) {
    for (name in new_default_options) {
      Overlay.default_options[name] = new_default_options[name];
    };
  },

  /**
   * Hides overlay instance containing given DOM element.
   * 
   * @param child DOM element
   * @public
   */
  hide : function(child) {
    for (var i = 0, length = Overlay.instances.length; i < length; i++) {
      if (Element.descendantOf(child, Overlay.instances[i].holder)) {
        Overlay.instances[i].hide();

        break;
      };
    };
  }
});

