import 'package:fish_redux/fish_redux.dart';

import '$prefix_effect.dart';
import '$prefix_reducer.dart';
import '$prefix_state.dart';
import '$prefix_view.dart';

class $nameComponent extends Component<$nameState> {
  $nameComponent()
      : super(
            effect: buildEffect(),
            reducer: buildReducer(),
            view: buildView,
            dependencies: Dependencies<$nameState>(
                adapter: null,
                slots: <String, Dependent<$nameState>>{
                }),);

}
