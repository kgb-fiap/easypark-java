document.querySelectorAll(".minute-input").forEach((input) => {
    const preview = document.getElementById(input.dataset.minutePreviewId);
    const updatePreview = () => {
        if (!preview) {
            return;
        }
        const minutes = Number(input.value);
        if (!Number.isFinite(minutes) || minutes < 60) {
            preview.textContent = "";
            return;
        }

        const hours = Math.floor(minutes / 60);
        const remainingMinutes = minutes % 60;
        preview.textContent = remainingMinutes === 0
                ? `${hours}h`
                : `${hours}h e ${remainingMinutes} min`;
    };

    input.addEventListener("input", updatePreview);
    updatePreview();
});
