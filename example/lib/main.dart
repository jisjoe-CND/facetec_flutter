import 'package:flutter/material.dart';
import 'package:facetec_flutter/facetec_flutter.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final String deviceKeyIdentifier = "dDw1XqV2CVDIgXdXkqDdjjUnMDhr2U3h";

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              TextButton(
                onPressed: () async {
                  try {
                    final res = await FacetecFlutter.initialize(deviceKeyIdentifier);
                    debugPrint('$res');
                  } catch (e) {
                    debugPrint(e.toString());
                  }
                },
                child: const Text('Initialise'),
              ),
              TextButton(
                onPressed: () async {
                  try {
                    final res = await FacetecFlutter.idCheck();
                    debugPrint('$res');
                  } catch (e) {
                    debugPrint(e.toString());
                  }
                },
                child: const Text('ID Check'),
              ),
              TextButton(
                onPressed: () async {
                  try {
                    final res = await FacetecFlutter.livenessCheck();
                    debugPrint('$res');
                  } catch (e) {
                    debugPrint(e.toString());
                  }
                },
                child: const Text('Liveness check'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
