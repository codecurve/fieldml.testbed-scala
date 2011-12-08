package framework.valuesource

import fieldml.evaluator._
import fieldml.valueType._
import scala.util.Random

import framework.value._

import framework.EvaluationState

class RandomEvaluator( val randomArgument : ArgumentEvaluator, valueType : EnsembleType )
    extends Evaluator( "random.0d.equiprobable", valueType )
    with ValueSource
{
    def variables = List()
    
    var cache : Option[Value] = None

    def isEventuallyUnbound( state : EvaluationState, arg : ArgumentEvaluator ) : Boolean =
    {
      val bind = state.getBind( arg )
      bind match {
        case Some(a : ArgumentEvaluator) => isEventuallyUnbound(state, a)
        case Some(_) => false
        case None => true
      }
    }

    def evaluate( state : EvaluationState ) : Option[Value] =
    {
      evaluateForType( state, valueType)
    }

    def evaluateForType( state : EvaluationState,
                         forType : ValueType ) : Option[Value] =
    {
      cache match {
        case None => {
          cache = if (isEventuallyUnbound( state, randomArgument )) {
            val number = math.abs(Random.nextInt()) % forType.asInstanceOf[EnsembleType].elementCount
            Some(EnsembleValue.apply( forType.asInstanceOf[EnsembleType],  number ))
          } else {
            println("Argument appears bound; random tag is identity function.");
            randomArgument.evaluate( state )
          }
          cache
        }
        case v@(Some(_)) => v
      }
    }
}
