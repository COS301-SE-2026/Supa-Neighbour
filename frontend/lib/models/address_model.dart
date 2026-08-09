class Address{
  final int addressid;

  Address({required this.addressid});

  factory Address.fromJson(Map<String, dynamic> json){
    return Address(addressid: json['addressid'] as int);
  }
}