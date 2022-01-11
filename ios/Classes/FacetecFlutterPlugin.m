#import "FacetecFlutterPlugin.h"
#if __has_include(<facetec_flutter/facetec_flutter-Swift.h>)
#import <facetec_flutter/facetec_flutter-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "facetec_flutter-Swift.h"
#endif

@implementation FacetecFlutterPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFacetecFlutterPlugin registerWithRegistrar:registrar];
}
@end
