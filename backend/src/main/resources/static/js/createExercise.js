document.addEventListener("DOMContentLoaded", () => {

    const form = document.querySelector("form");
    const nameInput = document.getElementById("exname");
    const descInput = document.getElementById("exdesc");

    const nameError = document.getElementById("nameError");
    const descError = document.getElementById("descError");

    const MIN_NAME_LENGTH = 3;
    const MIN_DESC_LENGTH = 10;

    function validateName() {
        const value = nameInput.value.trim();

        if (value.length < MIN_NAME_LENGTH) {
            nameError.textContent = `Name must be at least ${MIN_NAME_LENGTH} characters long`;
            return false;
        }

        if (value[0] !== value[0].toUpperCase()) {
            nameError.textContent = "Name must start with a capital letter";
            return false;
        }

        nameError.textContent = "";
        return true;
    }

    function validateDescription() {
        const value = descInput.value.trim();

        if (value.length < MIN_DESC_LENGTH) {
            descError.textContent = `Description must be at least ${MIN_DESC_LENGTH} characters long`;
            return false;
        }

        if (value[0] !== value[0].toUpperCase()) {
            descError.textContent = "Description must start with a capital letter";
            return false;
        }

        descError.textContent = "";
        return true;
    }

    nameInput.addEventListener("input", validateName);
    descInput.addEventListener("input", validateDescription);

    form.addEventListener("submit", (e) => {
        const isNameValid = validateName();
        const isDescValid = validateDescription();

        if (!isNameValid || !isDescValid) {
            e.preventDefault(); 
        }
    });

});