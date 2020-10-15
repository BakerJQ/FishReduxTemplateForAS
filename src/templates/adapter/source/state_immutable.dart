import 'package:fish_redux/fish_redux.dart';

class $nameState extends ImmutableSource implements Cloneable<$nameState> {
  @override
  $nameState clone() {
    return $nameState();
  }

  @override
  Object getItemData(int index) {
    // TODO: implement getItemData
    throw UnimplementedError();
  }

  @override
  String getItemType(int index) {
    // TODO: implement getItemType
    throw UnimplementedError();
  }

  @override
  // TODO: implement itemCount
  int get itemCount => throw UnimplementedError();

  @override
  ImmutableSource setItemData(int index, Object data) {
    // TODO: implement setItemData
    throw UnimplementedError();
  }
}

$nameState initState(Map<String, dynamic> args) {
  return $nameState();
}
