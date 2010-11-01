package test

import scala.collection.mutable.ArrayBuffer

import fieldml._
import fieldml.valueType._
import fieldml.valueType.bounds._

import fieldml.evaluator._

import framework.datastore._
import framework.value._
import framework._

import fieldml.jni.FieldmlApi._

import util._
import framework.region._

import java.io.FileWriter

object TestFieldml
{
    def main( argv : Array[String] ) : Unit =
    {
        val library = UserRegion.library
        
        val region = new UserRegion( "test" )

        val realType : ContinuousType = library.getObject( "library.real.1d" )
        val real3Type : ContinuousType = library.getObject( "library.real.3d" )
    
        val rc3ensemble : EnsembleType = library.getObject( "library.ensemble.rc.3d" )
        //This should be in the library
        val real3IndexVariable = region.createAbstractEvaluator( "library.ensemble.rc.3d", rc3ensemble )
       
        val rc3Type = region.createContinuousType( "test.domain.rc3" , rc3ensemble )

        val rc3ensemble2 : EnsembleType = library.getObject( "library.ensemble.rc.3d" )

        val rc3Type2 = region.createContinuousType( "test.domain.rc3_2.type" , rc3ensemble )
        
        val xi2dType : ContinuousType = library.getObject( "library.xi.2d" )

        val meshType = region.createMeshType( "test.mesh.type", 2, xi2dType.componentType )
        val meshVariable = region.createAbstractEvaluator( "test.mesh", meshType )

        val nodes = region.createEnsembleType( "test.nodes.type", 6, false )
        val nodesVariable = region.createAbstractEvaluator( "test.nodes", nodes )
        
        val bilinearParametersType : ContinuousType = library.getObject( "library.parameters.bilinear_lagrange" )
        //This should be in the library
        val bilinearIndexVariable = region.createAbstractEvaluator( "variables.bilinear.index", bilinearParametersType.componentType )
        
        region.set( xi2dType, 0.2, 0.2 )

        val rawInterpolator = region.createReferenceEvaluator( "test.interpolator_v0", "library.fem.bilinear_lagrange", library, realType )

        val firstInterpolator = region.createReferenceEvaluator( "test.interpolator_v1", "library.fem.bilinear_lagrange", library, realType )
        firstInterpolator.alias( xi2dType -> meshType.xiType )
        
        val secondInterpolator = region.createReferenceEvaluator( "test.interpolator_v2", "library.fem.bilinear_lagrange", library, realType )
        secondInterpolator.alias( xi2dType -> meshType.xiType )
        
        val parameterDescription = new SemidenseDataDescription( Array( nodesVariable, real3IndexVariable ), Array() )
        val parameterLocation = new InlineDataLocation()
        val parameters = region.createParameterEvaluator( "test.parameters", realType, parameterLocation, parameterDescription )
        
        parameters( 1, 1 ) = 0.0
        parameters( 2, 1 ) = 0.0
        parameters( 3, 1 ) = 0.0
        parameters( 4, 1 ) = 1.0
        parameters( 5, 1 ) = 1.0
        parameters( 6, 1 ) = 1.0
        
        parameters( 1, 2 ) = 0.0
        parameters( 2, 2 ) = 1.0
        parameters( 3, 2 ) = 2.0
        parameters( 4, 2 ) = 0.0
        parameters( 5, 2 ) = 1.0
        parameters( 6, 2 ) = 2.0

        parameters( 1, 3 ) = 1.0
        parameters( 2, 3 ) = 1.5
        parameters( 3, 3 ) = 2.0
        parameters( 4, 3 ) = 2.5
        parameters( 5, 3 ) = 3.0
        parameters( 6, 3 ) = 3.5
        
        println( parameters( 3, 1 ) )
        println( parameters( 2 ) )
        println( parameters( 1 ) )

        val elementVariable = region.createSubtypeEvaluator( meshVariable, "element" )
        
        val connectivityDescription = new SemidenseDataDescription( Array( elementVariable, bilinearIndexVariable ), Array() )
        val connectivityLocation = new InlineDataLocation()
        val connectivity = region.createParameterEvaluator( "test.connectivity", nodes, connectivityLocation, connectivityDescription )
        
        connectivity( 1, 1 ) = 1
        connectivity( 1, 2 ) = 4
        connectivity( 1, 3 ) = 2
        connectivity( 1, 4 ) = 5
        connectivity( 2, 1 ) = 2
        connectivity( 2, 2 ) = 5
        connectivity( 2, 3 ) = 3
        connectivity( 2, 4 ) = 6

        val piecewise = region.createPiecewiseEvaluator( "test.piecewise", meshType.elementType, realType )
        piecewise.map( 1 -> firstInterpolator )
        piecewise.map( 2 -> secondInterpolator )
        
        println( "*** piecewise(?) = " + region.getValue( piecewise ) )
        
        val bilinearParameters = region.createReferenceEvaluator( "test.bilinear_parameters", "test.parameters", region, realType ) 
        bilinearParameters.alias( nodes -> connectivity )
        
        piecewise.alias( bilinearParametersType -> bilinearParameters )
        
        region.set( meshType, 2, 0, 0 )

        println( "*****************************************************" )
        println( "*** piecewise(2) = " + region.getValue( piecewise ) )
        println( "*****************************************************" )
        
        region.set( meshType, 2, 1, 0 )

        println( "*****************************************************" )
        println( "*** piecewise(2) = " + region.getValue( piecewise ) )
        println( "*****************************************************" )

        region.set( meshType, 2, 0, 1 )

        println( "*****************************************************" )
        println( "*** piecewise(2) = " + region.getValue( piecewise ) )
        println( "*****************************************************" )
        
        region.set( meshType, 2, 1, 1 )

        println( "*****************************************************" )
        println( "*** piecewise(2) = " + region.getValue( piecewise ) )
        println( "*****************************************************" )

        val aggregate = region.createPiecewiseEvaluator( "test.aggregate", real3Type.componentType, real3Type )
        aggregate.map( 1 -> piecewise )
        aggregate.map( 2 -> piecewise )
        aggregate.map( 3 -> piecewise )
        
        region.set( meshType, 1, 0.5, 0.5 )

        println( "*****************************************************" )
        println( "*** aggregate(1) = " + region.getValue( aggregate ) )
        println( "*****************************************************" )
        
        
        region.serialize()
        
        val colladaXml = ColladaExporter.exportFromFieldML( region, 8, "test.mesh", "test.aggregate" )
        
        val f = new FileWriter( "collada two quads.xml" )
        f.write( colladaXml )
        f.close()

    }
}
