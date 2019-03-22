import 'package:fish_redux/fish_redux.dart';

import '../state.dart';
import 'reducer.dart';

class $nameAdapter extends DynamicFlowAdapter<$nameState> {
  $nameAdapter()
      : super(
          pool: <String, Component<Object>>{
          },
          connector: _$nameConnector(),
          reducer: buildReducer(),
        );
}

class _$nameConnector implements Connector<$nameState, List<ItemBean>> {
  @override
  List<ItemBean> get($nameState state) {
    return <ItemBean>[];
  }

  @override
  void set($nameState state, List<ItemBean> items) {
  }
}
