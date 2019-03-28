import 'package:fish_redux/fish_redux.dart';
import 'package:flutter/widgets.dart';

import 'effect.dart';
import 'reducer.dart';
import 'state.dart';

class $nameAdapter extends Adapter<$nameState> {
  $nameAdapter()
      : super(
          adapter: buildAdapter,
          effect: buildEffect(),
          reducer: buildReducer(),
        );
}

ListAdapter buildAdapter(
    $nameState state, Dispatch dispatch, ViewService service) {
  final List<IndexedWidgetBuilder> builders =
      Collections.compact(<IndexedWidgetBuilder>[]);
  return ListAdapter(
    (BuildContext buildContext, int index) =>
        builders[index](buildContext, index),
    builders.length,
  );
}
