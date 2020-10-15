import 'package:fish_redux/fish_redux.dart';

import '$prefix_effect.dart';
import '$prefix_reducer.dart';
import '$prefix_state.dart';
import '$prefix_view.dart';

class $namePage extends Page<$nameState, Map<String, dynamic>> {
  $namePage()
      : super(
            initState: initState,
            effect: buildEffect(),
            reducer: buildReducer(),
            view: buildView,
            dependencies: Dependencies<$nameState>(
                adapter: null,
                slots: <String, Dependent<$nameState>>{
                }),
            middleware: <Middleware<$nameState>>[
            ],);

}
