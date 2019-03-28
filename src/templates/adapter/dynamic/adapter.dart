import 'package:fish_redux/fish_redux.dart';

import 'reducer.dart';
import 'state.dart';

class $nameAdapter extends DynamicFlowAdapter<$nameState> {
  $nameAdapter()
      : super(
          pool: <String, Component<Object>>{
          },
          connector: _$nameConnector(),
          reducer: buildReducer(),
        );
}

class _$nameConnector extends ConnOp<$nameState, List<ItemBean>> {
  @override
  List<ItemBean> get($nameState state) {
    return <ItemBean>[];
  }

  @override
  void set($nameState state, List<ItemBean> items) {
  }

  @override
  subReducer(reducer) {
    // TODO: implement subReducer
    return super.subReducer(reducer);
  }
}
