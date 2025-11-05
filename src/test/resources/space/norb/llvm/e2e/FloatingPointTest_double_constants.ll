define double @double_constants() {
entry:
  %temp1 = add double 3.141593e+00, 2.718282e+00
  %result = mul double %temp1, 1.000000e+00
  ret double %result
}