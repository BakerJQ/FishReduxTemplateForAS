import 'package:fish_redux/fish_redux.dart';

import '$prefix_effect.dart';
import '$prefix_reducer.dart';
import '$prefix_state.dart';

class $nameAdapter extends SourceFlowAdapter<$nameState> {
  $nameAdapter()
      : super(
          pool: <String, Component<Object>>{},
          reducer: buildReducer(),
          effect: buildEffect(),
        );
}
