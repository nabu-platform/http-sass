// http://stackoverflow.com/a/27636915/4996531
console = {
    log: print,
    warn: print,
    error: print
};

// https://gist.github.com/salomvary/5a295d32e0868ffde42a
// Adopted from here: https://gist.github.com/bripkens/8597903
// Makes ES7 Promises polyfill work on Nashorn https://github.com/jakearchibald/es6-promise
// (Haven't verified how correct it is, use with care)
(function(context) {
    'use strict';

    var Phaser = Java.type('java.util.concurrent.Phaser');

    var phaser = new Phaser();

    var onTaskFinished = function() {
        phaser.arriveAndDeregister();
    };

    // if we enable timers (see other shim), the helper function call of Sass.compile is not guaranteed synchronous
    // we want the result synchronously, so just call it immediately
    // added bonus is that we don't have to kill the timer afterwards
    context.setTimeout = function(fn, millis /* [, args...] */) {
    	fn();
    	return function() {};
    };

    context.clearTimeout = function(cancel) {
        cancel();
    };

    context.setInterval = function(fn, delay /* [, args...] */) {
        var args = [].slice.call(arguments, 2, arguments.length);

        var cancel = null;

        var loop = function() {
            cancel = context.setTimeout(loop, delay);
            fn.apply(context, args);
        };

        cancel = context.setTimeout(loop, delay);
        return function() {
            cancel();
        };
    };

    context.clearInterval = function(cancel) {
        cancel();
    };

})(this);
