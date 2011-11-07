package util.library

class LinearLagrange( dimensions : Int )
    extends HomogeneousTensorBasis( dimensions )
{
    def basisFunction( xi : Double ) : Array[Double] =
    {
        Array( 1 - xi, xi )
    }
}
