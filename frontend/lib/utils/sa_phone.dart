class SaPhone {
  static final RegExp _pattern = RegExp(r'^(?:\+27|27|0)([1-8]\d{8})$');

  static String _clean(String input) =>
      input.replaceAll(RegExp(r'[\s\-\(\)]'), '');

  static bool isValid(String input) => _pattern.hasMatch(_clean(input));

  /// Returns +27XXXXXXXXX, or null if invalid.
  static String? toE164(String input) {
    final match = _pattern.firstMatch(_clean(input));
    return match == null ? null : '+27${match.group(1)}';
  }

  /// +27821234567 -> 082 123 4567
  static String display(String e164) {
    if (!e164.startsWith('+27') || e164.length != 12) return e164;
    final n = '0${e164.substring(3)}';
    return '${n.substring(0, 3)} ${n.substring(3, 6)} ${n.substring(6)}';
  }
}