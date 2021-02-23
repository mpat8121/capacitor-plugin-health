#import <Foundation/Foundation.h>
#import <Capacitor/Capacitor.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(HealthPlugin, "Health",
           CAP_PLUGIN_METHOD(echo, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(isAvailable, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(requestAuth, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(query, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(store, CAPPluginReturnPromise);
)
