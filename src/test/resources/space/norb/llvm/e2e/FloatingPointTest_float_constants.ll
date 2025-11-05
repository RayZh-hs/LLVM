define float @float_constants() {
entry:
  %temp1 = add float 3.140000e+00, 2.710000e+00
  %result = mul float %temp1, 1.000000e+00
  ret float %result
}