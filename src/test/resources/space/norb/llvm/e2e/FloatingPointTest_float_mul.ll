define float @float_mul(float %arg0, float %arg1) {
entry:
  %result = mul float %arg0, %arg1
  ret float %result
}