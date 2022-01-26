import Flutter
import UIKit
//import FaceTecSDK

public class SwiftFacetecFlutterPlugin: NSObject, FlutterPlugin {
//    var publicKey:String!
//    var licenseKeyIdentifier:String!
//    var errorMessages:String=""
//    var requestInProgress: Bool = false
//    var pendingResult:FlutterResult!
    
    var DeviceKeyIdentifier:String = ""
    
    static let BaseURL = "https://api.facetec.com/api/v3.1/biometrics"
    
    static let PublicFaceScanEncryptionKey =
        "-----BEGIN PUBLIC KEY-----\n" +
        "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5PxZ3DLj+zP6T6HFgzzk\n" +
        "M77LdzP3fojBoLasw7EfzvLMnJNUlyRb5m8e5QyyJxI+wRjsALHvFgLzGwxM8ehz\n" +
        "DqqBZed+f4w33GgQXFZOS4AOvyPbALgCYoLehigLAbbCNTkeY5RDcmmSI/sbp+s6\n" +
        "mAiAKKvCdIqe17bltZ/rfEoL3gPKEfLXeN549LTj3XBp0hvG4loQ6eC1E1tRzSkf\n" +
        "GJD4GIVvR+j12gXAaftj3ahfYxioBH7F7HQxzmWkwDyn3bqU54eaiB7f0ftsPpWM\n" +
        "ceUaqkL2DZUvgN0efEJjnWy5y1/Gkq5GGWCROI9XG/SwXJ30BbVUehTbVcD70+ZF\n" +
        "8QIDAQAB\n" +
    "-----END PUBLIC KEY-----"
    
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name:  "facetec_flutter", binaryMessenger: registrar.messenger())
    let instance = SwiftFacetecFlutterPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
      if (call.method == "getPlatformVersion") {
             result("iOS " + UIDevice.current.systemVersion)
             }
             else if(call.method == "initialize"){
//        let licenseKeyIdentifiers = call.arguments as? Array<String>
//
//        licenseKeyIdentifier=licenseKeyIdentifier?[0] as? String ?? ""
//
//                FaceTec.sdk.initializeInDevelopmentMode(deviceKeyIdentifier: Config.DeviceKeyIdentifier, faceScanEncryptionKey: Config.PublicFaceScanEncryptionKey, completion: { initializationSuccessful in
//                    completion(initializationSuccessful)
//
//                    if(initializationSuccessful) {
//                        result(status);
//                    }else{
//                        result(status)
//                    }
//                })
//
//            Zoom.sdk.initialize(licenseKeyIdentifier: ZoomGlobalState.DeviceLicenseKeyIdentifier,faceMapEncryptionKey: ZoomGlobalState.PublicFaceMapEncryptionKey, completion: { initializationSuccessful in
//                let status=Zoom.sdk.description(for: Zoom.sdk.getStatus())
//
//                if(initializationSuccessful) {
//                    result(status);
//                }else{
//                    result(status)
//                }
                 //            })
    }
  }
}
