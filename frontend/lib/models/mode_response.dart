class ModeResponse{
  final String mode;

  ModeResponse({required this.mode});

  factory ModeResponse.fromJson(Map<String, dynamic> json){
    return ModeResponse(mode: json['mode'] as String);
  }

  Map<String, dynamic> toJson() => {'mode' : mode};
}