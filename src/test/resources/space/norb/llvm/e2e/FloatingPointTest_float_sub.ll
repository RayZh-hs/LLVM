define float @float_sub(float %arg0, float %arg1) {
entry:
  %result = sub float %arg0, %arg1
  ret float %result
}