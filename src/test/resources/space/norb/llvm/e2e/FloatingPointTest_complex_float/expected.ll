define float @complex_float_ops(float %arg0, float %arg1, float %arg2) {
entry:
  %temp1 = mul float %arg0, %arg1
  %temp2 = add float %temp1, %arg2
  %result = mul float %temp2, 5.000000e-01
  ret float %result
}