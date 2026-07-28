import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../services/settings_service.dart';

final settingsServiceProvider = Provider((ref) => SettingsService());
class ThemeModeNotifier extends Notifier<ThemeMode> {
  @override
  ThemeMode build(){
    _loadFromServer();
    return ThemeMode.light;
  }

  Future<void> _loadFromServer() async{
    try{
      final response = await ref.read(settingsServiceProvider).getMode();
      state = response.mode == 'dark' ? ThemeMode.dark : ThemeMode.light;

    }catch(_){

    }
  }

  Future<void> toggleDarkMode(bool enabled) async{
    final newMode = enabled ? ThemeMode.dark : ThemeMode.light;

    state = newMode;

    try{
      await ref.read(settingsServiceProvider).setMode(enabled ? 'dark' : 'light');
    }catch(_){
      state = enabled ? ThemeMode.dark : ThemeMode.light;
      rethrow;
    }
  }
}

final themeModeProvider = NotifierProvider<ThemeModeNotifier, ThemeMode>(ThemeModeNotifier.new);