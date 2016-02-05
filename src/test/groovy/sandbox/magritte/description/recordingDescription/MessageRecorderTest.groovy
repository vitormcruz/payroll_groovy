package sandbox.magritte.description.recordingDescription
import org.junit.Test
import sandbox.magritte.description.util.*
import sandbox.validator.imp.ValidationException

import static groovy.test.GroovyAssert.shouldFail

class MessageRecorderTest {

    @Test
    def void "Create a MessageRecorder with null delegate class"(){
        ValidationException ex = shouldFail(ValidationException, {new MessageRecorder(null)})
        assert ex.hasError("No interface to record was specified")
    }

    @Test
    def void "Create a MessageRecorder with concrete delegate class"(){
        ValidationException ex = shouldFail(ValidationException, {new MessageRecorder(String)})
        assert ex.hasError("You specified the class String, but I can only record interfaces")
    }

    @Test
    def void "Create a MessageRecorder with abstract delegate class"(){
        ValidationException ex = shouldFail(ValidationException, {new MessageRecorder(AbstractClassForRecording)})
        assert ex.hasError("You specified the class AbstractClassForRecording, but I can only record interfaces")
    }

    @Test
    def void "Call an inexistent method throw common error"(){
        def MissingMethodException ex = shouldFail(MissingMethodException, {new MessageRecorder(InterfaceForRecording).inexistentMethod()})
        assert ex.method == "inexistentMethod"
        assert ex.type == InterfaceForRecording
    }

    @Test
    def void "Record should return an instance of the recorded class"(){
        def classRecorder = new MessageRecorder(InterfaceForRecording).interface_method1()
        assert classRecorder instanceof InterfaceForRecording
    }

    @Test
    def void "Record abstract methods from traits"(){
        def classRecorder = new MessageRecorder(TraitForRecording)
        classRecorder.trait_method1()
        assert classRecorder.recordedMethods.find {it.name == "trait_method1"} : "trait_method1 wasn't recorded"
    }

    @Test
    def void "Record abstract methods from a varied hierachy of interfaces (traits and interfaces)"(){
        def classRecorder = new MessageRecorder(InterfaceWithHierarchyForRecording)
        classRecorder.interface_method1()
        classRecorder.trait_method1()
        assert classRecorder.recordedMethods.find {it.name == "interface_method1"} : "interface_method1 wasn't recorded"
        assert classRecorder.recordedMethods.find {it.name == "trait_method1"} : "trait_method1 wasn't recorded"
    }

    @Test
    def void "Playback methods in the called order with the right arguments"(){
        def classRecorder = new MessageRecorder(InterfaceWithHierarchyForRecording)
        classRecorder.interface_method1()
        classRecorder.trait_method1()
        classRecorder.interface_method1()
        classRecorder.trait_method1()
        classRecorder.trait_method1()
        classRecorder.interface_method1()
        classRecorder.interface_method2("teste")
        classRecorder.interface_method3("teste", 1)
        def expectedDate = new Date()
        classRecorder.interface_method4("teste", expectedDate, 1, 2, 3 )

        def playbackVerifier = new PlaybackVerifier()
        playbackVerifier.expectedMethodOrder(["interface_method1", "trait_method1", "interface_method1", "trait_method1",
                                              "trait_method1", "interface_method1", "interface_method2",
                                              "interface_method3", "interface_method4"])

        playbackVerifier.expectedArgumentOrder([[], [], [], [], [], [],
                                               ["teste"], ["teste", 1], ["teste", expectedDate, 1, 2, 3]])

        classRecorder.playbackAt(playbackVerifier)
        playbackVerifier.verifyPlayback()
    }

    @Test
    def void "Playback methods in a concrete class"(){
        def classRecorder = new MessageRecorder(InterfaceForRecording)

        classRecorder.interface_method1();
        classRecorder.interface_method2(1);
        classRecorder.interface_method3(2, 3);
        classRecorder.interface_method4("teste", new Date(), 1, 2, 3)

        classRecorder.playbackAt(new InterfaceForRecordingImpl())
    }

    @Test
    def void "asTypeBeeingRecorded should return a MessageRecorder disguised (proxy) as the type it is recording"(){
        def messageCallRecorder = new MessageRecorder(InterfaceForRecording)
        assert messageCallRecorder.asTypeBeingRecorded() instanceof InterfaceForRecording :
        "The object returned by asTypeBeingRecorded is not the correct one."
    }
}