import 'package:fish_redux/fish_redux.dart';

import 'effect.dart';
import 'reducer.dart';
import 'state.dart';

class $nameAdapter extends StaticFlowAdapter<$nameState> {
  $nameAdapter()
      : super(
          slots:<Dependent<$nameState>>[

          ],
          effect: buildEffect(),
          reducer: buildReducer(),
        );
}
