
import 'dart:async';

import 'package:flutter/services.dart';

class FacetecFlutter {
  static const MethodChannel _channel = MethodChannel('facetec_flutter');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String?> setServerUrl(String serverUrl) async {
    final String? result = await _channel.invokeMethod('setServerUrl', serverUrl);
    return result;
  }

  static Future<String?> initialize(String appToken) async {
    final String? result = await _channel.invokeMethod('initialize', [appToken]);
    return result;
  }
}
