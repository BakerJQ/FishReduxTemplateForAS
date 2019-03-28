import 'package:fish_redux/fish_redux.dart';

import 'effect.dart';
import 'reducer.dart';
import 'state.dart';
import 'view.dart';

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
