import com.google.api.services.toolresults.model.Step
import com.google.api.services.toolresults.model.StepDimensionValueEntry
import com.google.testing.model.ToolResultsStep

import Billing.billableMinutes
import Constants.projectId

internal class ToolResultsValue(step: Step, var toolResultsStep: ToolResultsStep) {
    var webLink: String = ""
    var billableMinutes: Long = 0
    var testDurationSeconds: Long = 0
    var runDurationSeconds: Long = 0
    var name: String
    var targets: String
    var dimensions: List<StepDimensionValueEntry>
    var outcome: String

    init {
        updateWebLink()
        billableMinutes = billableMinutes(testDurationSeconds)

        val executionStep = step.testExecutionStep
        testDurationSeconds = executionStep.testTiming.testProcessDuration.seconds!!

        runDurationSeconds = step.runDuration.seconds!!
        name = step.name
        targets = step.description
        dimensions = step.dimensionValue
        // "failure" or "success"
        outcome = step.outcome.summary
    }

    private fun updateWebLink() {
        webLink = "https://console.firebase.google.com/project/$projectId/" +
                "testlab/histories/${toolResultsStep.historyId}/" +
                "matrices/${toolResultsStep.executionId}"
    }

    override fun toString(): String {
        val dimensionSb = StringBuilder()

        for (dimension in dimensions) {
            dimensionSb
                    .append("  ")
                    .append(dimension.key)
                    .append(": ")
                    .append(dimension.value)
                    .append("\n")
        }

        val dimensionString = dimensionSb.toString()

        return """Billable minutes: ${billableMinutes}m
        Test duration: ${testDurationSeconds}s
        "Run duration: ${runDurationSeconds}s
        "Name: ${name}
        Targets: ${targets}
        Dimensions: $dimensionString
        Outcome: ${outcome}
        $webLink"""
    }
}
