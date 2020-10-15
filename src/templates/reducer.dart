import 'package:fish_redux/fish_redux.dart';

import '$prefix_action.dart';
import '$prefix_state.dart';

Reducer<$nameState> buildReducer() {
  return asReducer(
    <Object, Reducer<$nameState>>{
      $nameAction.action: _onAction,
    },
  );
}

$nameState _onAction($nameState state, Action action) {
  final $nameState newState = state.clone();
  return newState;
}
