define double @double_add(double %arg0, double %arg1) {
entry:
  %result = add double %arg0, %arg1
  ret double %result
}