define double @complex_double_ops(double %arg0, double %arg1, double %arg2) {
entry:
  %temp1 = mul double %arg0, %arg1
  %temp2 = add double %temp1, %arg2
  %result = mul double %temp2, 5.000000e-01
  ret double %result
}