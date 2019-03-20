package moe.gogo.check

import moe.gogo.Case
import java.io.File

abstract class Checker {

    abstract fun check(case: Case, output: File): CheckResult

}

