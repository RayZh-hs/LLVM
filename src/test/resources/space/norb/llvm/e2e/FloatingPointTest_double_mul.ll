define double @double_mul(double %arg0, double %arg1) {
entry:
  %result = mul double %arg0, %arg1
  ret double %result
}