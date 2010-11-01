package framework.region

import scala.collection.mutable.Map

import fieldml.FieldmlObject
import fieldml.valueType._
import fieldml.valueType.bounds._

import util.exception._

import framework.Context
import framework.EvaluationState
import framework.value.Value
import framework.value.ContinuousValue
import framework.value.EnsembleValue
import framework.value.MeshValue

abstract class Region( val name : String )
{
    val context = new Context( name )

    
    protected val objects = Map[String, FieldmlObject]()
    
    
    //TODO Use region names
    def getObject[A <: FieldmlObject]( objectName : String ) : A =
    {
        val result = objects.get( objectName )
        
        result match
        {
            case s : Some[A] => return s.get
            case s : Some[_] => throw new FmlTypeException( s.get, null ) //MUSTDO Fix
            case None => throw new FmlUnknownObjectException( objectName, name )
        }
    }
    
    
    private def evaluate( obj : FieldmlObject ) : Option[Value] =
    {
        val state = new EvaluationState()
        
        state.push( context )
        
        return state.get( obj )
    }
    
    
    def getValue( obj : FieldmlObject ) : Option[Value] =
    {
        return evaluate( obj )
    }
    
    
    def getValue( obj : ContinuousType ) : Option[ContinuousValue] =
    {
        evaluate( obj ) match
        {
            case v : Some[ContinuousValue] => return v
            case _ => return None
        }
    }
    
    
    def getValue( obj : EnsembleType ) : Option[EnsembleValue] =
    {
        evaluate( obj ) match
        {
            case v : Some[EnsembleValue] => return v
            case _ => return None
        }
    }
    
    
    def getValue( obj : MeshType ) : Option[MeshValue] =
    {
        evaluate( obj ) match
        {
            case v : Some[MeshValue] => return v
            case _ => return None
        }
    }
}
