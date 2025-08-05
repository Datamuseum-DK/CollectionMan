//    import QrScanner from "qr-scanner.min.js";

    const video = document.getElementById('qr-video');
    const videoContainer = document.getElementById('video-container');
    const camHasCamera = document.getElementById('cam-has-camera');
    const camList = document.getElementById('cam-list');
    const camHasFlash = document.getElementById('cam-has-flash');
    const flashToggle = document.getElementById('flash-toggle');
    const flashState = document.getElementById('flash-state');
    const camQrResult = document.getElementById('cam-qr-result');

    function setResult(label, result) {
        //console.log(result.data);
        label.textContent = result.data;
        label.style.color = 'teal';
        clearTimeout(label.highlightTimeout);
        label.highlightTimeout = setTimeout(() => label.style.color = 'inherit', 100);
        videoContainer.style.display = 'none';
        document.getElementById("result").value = result.data;
        scanner.stop();
        document.getElementById("doquery").submit();
    }

    // ####### Web Cam Scanning #######

    const scanner = new QrScanner(video, result => setResult(camQrResult, result), {
        onDecodeError: error => {
            camQrResult.textContent = error;
            camQrResult.style.color = 'inherit';
        },
        highlightScanRegion: true,
        highlightCodeOutline: true,
    });

    const updateFlashAvailability = () => {
        scanner.hasFlash().then(hasFlash => {
            camHasFlash.textContent = hasFlash;
            flashToggle.style.display = hasFlash ? 'inline-block' : 'none';
        });
    };

    scanner.start().then(() => {
        updateFlashAvailability();
        // List cameras after the scanner started to avoid listCamera's stream and the scanner's stream being requested
        // at the same time which can result in listCamera's unconstrained stream also being offered to the scanner.
        // Note that we can also start the scanner after listCameras, we just have it this way around in the demo to
        // start the scanner earlier.
        QrScanner.listCameras(true).then(cameras => cameras.forEach(camera => {
            const option = document.createElement('option');
            option.value = camera.id;
            option.text = camera.label;
            camList.add(option);
        }));
    });

    QrScanner.hasCamera().then(hasCamera => camHasCamera.textContent = hasCamera);

    // for debugging
    window.scanner = scanner;

    // document.getElementById('show-scan-region').addEventListener('change', (e) => {
    //     const input = e.target;
    //     const label = input.parentNode;
    //     label.parentNode.insertBefore(scanner.$canvas, label.nextSibling);
    //     scanner.$canvas.style.display = input.checked ? 'block' : 'none';
    // });

    camList.addEventListener('change', event => {
        scanner.setCamera(event.target.value).then(updateFlashAvailability);
    });

    flashToggle.addEventListener('click', () => {
        scanner.toggleFlash().then(() => flashState.textContent = scanner.isFlashOn() ? 'on' : 'off');
    });

    document.getElementById('start-button').addEventListener('click', () => {
        //camQrResult.textContent = error;
        videoContainer.style.display = 'block';
        fundet.style.display = 'none';
        opretartefakt.style.display = 'none';
        scanner.start();
    });

    document.getElementById('stop-button').addEventListener('click', () => {
        videoContainer.style.display = 'none';
        fundet.style.display = 'none';
        opretartefakt.style.display = 'none';
        scanner.stop();
    });

