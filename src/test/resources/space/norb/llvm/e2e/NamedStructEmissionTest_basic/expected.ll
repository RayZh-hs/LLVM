; Module: NamedStructEmissionTest

%OpaquePoint = type { i64, i64 }
%Point = type { i32, i32 }

@globalPoint = global %Point zeroinitializer

define %Point @processPoints(%Point %arg0, %OpaquePoint %arg1) {
entry:
  %localPoint = alloca %Point
  store %Point %arg0, ptr %localPoint
  %loadedPoint = load %Point, ptr %localPoint
  ret %Point %loadedPoint
}