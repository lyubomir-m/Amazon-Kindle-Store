const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const csrfHeaderName = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');


function openLoginModal() {
    document.getElementById('loginModal').style.display = 'block';
    openBackdrop();
}

function closeLoginModal() {
    document.getElementById('loginModal').style.display = 'none';
    closeBackdrop();
}

//Close Login Modal
document.addEventListener('DOMContentLoaded', function () {
    let button = document.getElementById('loginModalCloseButton');
    if (button) {
        button.addEventListener('click', closeLoginModal);
    }
})

function openBackdrop() {
    document.getElementById('modalBackdrop').style.display = 'block';
}

function closeBackdrop() {
    document.getElementById('modalBackdrop').style.display = 'none';
}

// Open Rating Modal
document.addEventListener('DOMContentLoaded', function () {
    let button = document.querySelector('.open-rating-modal-btn');
    if (button) {
        button.addEventListener('click', function () {
            if (!isLoggedIn) {
                openLoginModal();
                return;
            }
            let bookId = this.getAttribute('data-book-id');
            let imgUrl = this.getAttribute('data-img-url');

            fetch(`/books/${bookId}/check-rating`)
                .then(response => {
                    return response.text().then(text => {
                        return {text: text, ok: response.ok};
                    });
                })
                .then(result => {
                    if (!result.ok) {
                        throw new Error(result.text);
                    }
                    document.getElementById('resultMessagesRating').textContent = '';
                    document.getElementById('errorMessagesRating').textContent = '';
                    document.getElementById('ratingModal').style.display = 'block';
                    document.getElementById('rating-modal-image').src = imgUrl;
                    openBackdrop();
                })
                .catch(error => {
                    document.getElementById('commonModalText').textContent = '';
                    document.getElementById('commonModalErrors').textContent = error.message;
                    document.getElementById('common-modal-image').src = imgUrl;
                    openCommonModal();
                });
        });
    }
});

// Rating Modal: Close, Submit buttons
document.addEventListener('DOMContentLoaded', function () {
    let closeButton = document.getElementById('close-rating-btn');
    let submitButton = document.getElementById('submit-rating-btn');
    const modal = document.getElementById('ratingModal');
    const resultText = document.getElementById('resultMessagesRating');
    const errorText = document.getElementById('errorMessagesRating');

    if (closeButton) {
        closeButton.addEventListener('click', function () {
            modal.style.display = 'none';
            closeBackdrop();
        });
    }


    if (submitButton) {
        submitButton.addEventListener('click', function () {
            let rating = document.getElementById('bookRating').value;
            if (rating === '') {
                errorText.textContent = 'Please select a rating.';
                return;
            }

            let ratingData = {rating: rating};
            let bookId = this.getAttribute('data-book-id');
            fetch(`/books/${bookId}/rate`, {
                method: 'POST', headers: {
                    'Content-Type': 'application/json', [csrfHeaderName]: csrfToken
                }, body: JSON.stringify(ratingData)
            })
                .then(response => {
                    if (!response.ok) {
                        return response.text().then(text => {
                            throw new Error(text);
                        });
                    }
                    return response.json();
                })
                .then(data => {
                    errorText.textContent = '';
                    resultText.textContent = 'Your rating has been submitted successfully.';
                    document.getElementById('averageRating').textContent = data.averageRating.toFixed(1);
                    document.getElementById('ratingsCount').textContent = data.ratingsCount
                        .toLocaleString('en-US', {minimumFractionDigits: 0}).replace(/,/g, ' ');
                })
                .catch(error => {
                    resultText.textContent = '';
                    errorText.textContent = error.message;
                });
        });
    }

});

// Open Review Modal
document.addEventListener('DOMContentLoaded', function () {
    let button = document.querySelector('.open-review-modal-btn');
    if (button) {
        button.addEventListener('click', function () {
            if (!isLoggedIn) {
                openLoginModal();
                return
            }

            let bookId = this.getAttribute('data-book-id');
            let imgUrl = this.getAttribute('data-img-url');
            const resultText = document.getElementById('resultMessagesReview');
            const errorText = document.getElementById('errorMessagesReview');

            fetch(`/books/${bookId}/check-review`)
                .then(response => {
                    if (!response.ok) {
                        return response.text().then(text => {
                            throw new Error(text);
                        });
                    }
                    return response.text();
                })
                .then(() => {
                    resultText.textContent = '';
                    errorText.textContent = '';
                    document.getElementById('review-modal-image').src = imgUrl;
                    document.getElementById('reviewModal').style.display = 'block';
                    openBackdrop();
                })
                .catch(error => {
                    document.getElementById('commonModalText').textContent = '';
                    document.getElementById('commonModalErrors').textContent = error.message;
                    document.getElementById('common-modal-image').src = imgUrl;
                    openCommonModal();
                });
        });
    }
});

//Review Modal: Close, Submit buttons
document.addEventListener('DOMContentLoaded', function () {
    const closeButton = document.getElementById('close-review-btn');
    const submitButton = document.getElementById('submit-review-btn');
    const modal = document.getElementById('reviewModal');
    const resultText = document.getElementById('resultMessagesReview');
    const errorText = document.getElementById('errorMessagesReview');

    if (closeButton) {
        closeButton.addEventListener('click', function () {
            modal.style.display = 'none';
            closeBackdrop();
        });
    }

    if (submitButton) {
        submitButton.addEventListener('click', function () {
            let reviewTitle = document.getElementById('reviewTitle').value;
            let reviewText = document.getElementById('reviewText').value;
            let reviewRating = document.getElementById('reviewRating').value;
            let bookId = this.getAttribute('data-book-id');

            if (!reviewTitle || !reviewText || reviewRating === '') {
                errorText.textContent = 'Please fill out all required fields.';
                return;
            }

            let reviewData = {
                reviewTitle: reviewTitle, reviewText: reviewText, reviewRating: parseInt(reviewRating)
            }

            fetch(`/books/${bookId}/review`, {
                method: 'POST', headers: {
                    'Content-Type': 'application/json', [csrfHeaderName]: csrfToken
                }, body: JSON.stringify(reviewData)
            })
                .then(response => {
                    if (!response.ok) {
                        return response.text().then(text => {
                            throw new Error(text);
                        });
                    }
                    return response.json();
                })
                .then(data => {
                    updateReviewUI(data);
                    errorText.textContent = ''
                    resultText.textContent = 'Your review has been submitted successfully.';
                })
                .catch(error => {
                    resultText.textContent = '';
                    errorText.textContent = error.message;
                });
        });
    }
});

function updateReviewUI(data) {
    document.getElementById('averageRating').textContent = data.averageRating.toFixed(1);
    document.getElementById('ratingsCount').textContent = data.ratingsCount
        .toLocaleString('en-US', {minimumFractionDigits: 0}).replace(/,/g, ' ');

    let newReview = document.createElement('div');
    newReview.classList.add('review', 'row', 'justify-content-center');

    newReview.innerHTML = `
        <div class="picture-container col-2 d-flex justify-content-end">
                <img src="${data.pictureBase64}" style="margin-right: 35px"/>
            </div>
            <div class="review-details col-5 d-flex">
                <div class="review-container-1">
                    <div class="review-container-2">
                        <div class="reviewer-name">${data.firstName} ${data.lastName}</div>
                        <div class="review-rating">${generateStars(data.ratingValue)}</div>
                        <div class="review-title">${data.title}</div>
                        <div class="review-date">Reviewed on ${formatDate(data.submissionDate)}</div>
                    </div>

                    <div class="review-text">
                        <div>${data.text}</div>
                    </div>
                </div>
            </div>
    `;

    let reviewList = document.querySelector('.review-list');
    reviewList.insertBefore(newReview, reviewList.firstChild);

    let reviews = document.querySelectorAll('.review-list .review');
    if (reviews.length >= 11) {
        reviews[reviews.length - 1].remove();
    }
}

function generateStars(ratingValue) {
    let starsHtml = '';
    for (let i = 1; i <= ratingValue; i++) {
        starsHtml += '<span style="margin-right: 4px"><img class="star" src="/stars/full-star.jpeg" alt="Full Star"></span>';
    }
    for (let i = ratingValue + 1; i <= 5; i++) {
        starsHtml += '<span style="margin-right: 4px"><img class="star" src="/stars/empty-star.png" alt="Empty Star"></span>';
    }
    return starsHtml;
}

function formatDate(dateStr) {
    const date = new Date(dateStr);
    const options = {year: 'numeric', month: 'long', day: 'numeric'};
    return date.toLocaleDateString(undefined, options);
}

// Register User Button
document.addEventListener('DOMContentLoaded', function () {
    const modalText = document.getElementById('commonModalText');
    const modalErrors = document.getElementById('commonModalErrors');
    const modalCloseButton = document.getElementById('commonModalCloseButton');
    let registrationSuccess = false;

    const openRegistrationModal = function(event) {
        event.stopPropagation();

        const email = document.getElementById('email').value;
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        const firstName = document.getElementById('firstName').value;
        const lastName = document.getElementById('lastName').value;
        const age = parseInt(document.getElementById('age').value);

        if (!email || !username || !password || !firstName || !lastName || isNaN(age)) {
            modalText.textContent = '';
            modalErrors.textContent = 'Please fill in all fields correctly.';
            openCommonModal();
            return;
        }

        if (password !== confirmPassword) {
            modalText.textContent = '';
            modalErrors.textContent = 'The passwords do not match.';
            openCommonModal();
            return;
        }

        const userData = {
            email: email,
            username: username,
            password: password,
            confirmPassword: confirmPassword,
            firstName: firstName,
            lastName: lastName,
            age: age
        };

        fetch('/users/register', {
            method: 'POST', headers: {
                'Content-Type': 'application/json',
                [csrfHeaderName]: csrfToken
            }, body: JSON.stringify(userData)
        })
            .then(response => {
                if (!response.ok) {
                    return response.text().then(text => {
                        throw new Error(text);
                    });
                }
                return response.text();
            })
            .then(result => {
                registrationSuccess = true;
                modalErrors.textContent = '';
                modalText.textContent = 'You have been registered successfully.';
                openCommonModal();
            })
            .catch(error => {
                modalText.textContent = '';
                modalErrors.textContent = error.message;
                openCommonModal();
            });
    };

    const registerUserButton = document.getElementById('register-user-btn');

    if (registerUserButton) {
        registerUserButton.addEventListener('click', openRegistrationModal);

        modalCloseButton.addEventListener('click', function () {
            if (registrationSuccess) {
                window.location.href = '/users/login';
            }
        });
    }
});


function openCommonModal() {
    document.getElementById('commonModal').style.display = 'block';
    openBackdrop();
}

function closeCommonModal() {
    document.getElementById('commonModal').style.display = 'none';
    closeBackdrop();
}

// Close Common Modal
document.addEventListener('DOMContentLoaded', function () {
    const closeButton = document.getElementById('commonModalCloseButton');
    if (closeButton) {
        closeButton.addEventListener('click', closeCommonModal);
    }
});


// Buy Now Buttons
document.addEventListener('DOMContentLoaded', function () {
    const buttons = document.querySelectorAll('.buy-now-btn');
    const modalText = document.getElementById('commonModalText');
    const modalErrors = document.getElementById('commonModalErrors');
    const img = document.getElementById('common-modal-image');


    buttons.forEach(button => {
        button.addEventListener('click', function () {
            if (!isLoggedIn) {
                openLoginModal();
                return;
            }

            const bookId = this.getAttribute('data-book-id');
            const imgUrl = this.getAttribute('data-img-url');
            fetch(`/books/${bookId}/purchase`, {
                method: 'POST', headers: {
                    [csrfHeaderName]: csrfToken
                }
            })
                .then(response => {
                    return response.text().then(text => {
                        return {text: text, ok: response.ok};
                    });
                })
                .then(result => {
                    if (!result.ok) {
                        throw new Error(result.text);
                    }
                    img.src = imgUrl;
                    modalErrors.textContent = '';
                    modalText.textContent = result.text;
                    openCommonModal();
                })
                .catch(error => {
                    img.src = imgUrl;
                    modalText.textContent = '';
                    modalErrors.textContent = error.message;
                    openCommonModal();
                });
        });
    });
});

// Add to Cart Button
document.addEventListener('DOMContentLoaded', function () {
    let button = document.querySelector('.add-to-cart-btn');
    const modalText = document.getElementById('commonModalText');
    const modalErrors = document.getElementById('commonModalErrors');

    if (button) {
        button.addEventListener('click', function () {
            if (!isLoggedIn) {
                openLoginModal();
                return;
            }

            let bookId = button.getAttribute('data-book-id');
            let imgUrl = button.getAttribute('data-img-url');
            fetch(`/books/${bookId}/cart`, {
                method: 'POST', headers: {
                    [csrfHeaderName]: csrfToken
                }
            })
                .then(response => {
                    return response.text().then(text => {
                        return {text: text, ok: response.ok};
                    });
                })
                .then(result => {
                    if (!result.ok) {
                        throw new Error(result.text);
                    }
                    document.getElementById('common-modal-image').src = imgUrl;
                    modalErrors.textContent = '';
                    modalText.textContent = 'The book was added to your shopping cart.';
                    document.getElementById('numberOfBooksInCart').textContent = result.text;
                    openCommonModal();
                })
                .catch(error => {
                    document.getElementById('common-modal-image').src = imgUrl;
                    modalText.textContent = '';
                    modalErrors.textContent = error.message;
                    openCommonModal();
                });
        });
    }
});

// Add to Your List Button
document.addEventListener('DOMContentLoaded', function () {
    let button = document.querySelector('.add-to-your-list-btn');
    const modalText = document.getElementById('commonModalText');
    const modalErrors = document.getElementById('commonModalErrors');
    const img = document.getElementById('common-modal-image');

    if (button) {
        button.addEventListener('click', function () {
            if (!isLoggedIn) {
                openLoginModal();
                return;
            }

            let bookId = button.getAttribute('data-book-id');
            let imgUrl = button.getAttribute('data-img-url');
            fetch(`/books/${bookId}/list`, {
                method: 'POST', headers: {
                    [csrfHeaderName]: csrfToken
                }
            })
                .then(response => {
                    return response.text().then(text => {
                        return {text: text, ok: response.ok};
                    });
                })
                .then(result => {
                    img.src = imgUrl;
                    if (!result.ok) {
                        throw new Error(result.text);
                    }
                    modalErrors.textContent = '';
                    modalText.textContent = result.text;
                    openCommonModal();
                })
                .catch(error => {
                    modalText.textContent = '';
                    modalErrors.textContent = error.message;
                    openCommonModal();
                });
        });
    }
});

// Update and Delete Review Buttons
document.addEventListener('DOMContentLoaded', function () {
    const updateButtons = document.querySelectorAll('.update-review-btn');
    updateButtons.forEach(button => {
        button.addEventListener('click', function () {
            const bookId = this.getAttribute('data-book-id');
            const rating = this.getAttribute('data-rating');
            const title = this.getAttribute('data-title');
            const text = this.getAttribute('data-text');
            const imgUrl = this.getAttribute('data-img-url');

            document.getElementById('review-modal-image').src = imgUrl;
            document.getElementById('reviewRating').value = rating;
            document.getElementById('reviewTitle').value = title;
            document.getElementById('reviewText').value = text;

            const updateButton = document.getElementById('update-review-btn');
            updateButton.setAttribute('data-book-id', bookId);

            document.getElementById('errorMessagesReview').textContent = '';
            document.getElementById('resultMessagesReview').textContent = '';
            document.getElementById('reviewModal').style.display = 'block';
            openBackdrop();
        });
    });


    const deleteButtons = document.querySelectorAll('.delete-review-btn');
    deleteButtons.forEach(button => {
        button.addEventListener('click', function () {
            const bookId = this.getAttribute('data-book-id');
            const modalText = document.getElementById('commonModalText');
            const modalErrors = document.getElementById('commonModalErrors');
            const img = document.getElementById('common-modal-image');
            const imgUrl = this.getAttribute('data-img-url');


            fetch(`/books/${bookId}/review`, {
                method: 'DELETE', headers: {
                    [csrfHeaderName]: csrfToken
                }
            })
                .then(response => {
                    return response.text().then(text => {
                        return {text: text, ok: response.ok};
                    });
                })
                .then(result => {
                    img.src = imgUrl;
                    if (!result.ok) {
                        throw new Error(result.text);
                    }
                    updateResultCount();
                    document.getElementById(bookId).textContent = 'This review was deleted.';
                    modalErrors.textContent = '';
                    modalText.textContent = result.text;
                    openCommonModal();
                })
                .catch(error => {
                    modalText.textContent = '';
                    modalErrors.textContent = error.message;
                    openCommonModal();
                });
        });
    });

    const updateButton = document.getElementById('update-review-btn');
    if (updateButton) {
        let reviewUpdateSuccess = false;
        updateButton.addEventListener('click', function () {
            const bookId = this.getAttribute('data-book-id');
            const reviewRating = document.getElementById('reviewRating').value;
            const reviewTitle = document.getElementById('reviewTitle').value;
            const reviewText = document.getElementById('reviewText').value;

            if (!reviewTitle || !reviewText || reviewRating === '') {
                document.getElementById('errorMessagesReview').textContent = 'Please fill out all required fields.';
                return;
            }
            const reviewData = {reviewRating, reviewTitle, reviewText};


            fetch(`/books/${bookId}/review/`, {
                method: 'PUT', headers: {
                    'Content-Type': 'application/json', [csrfHeaderName]: csrfToken
                }, body: JSON.stringify(reviewData)
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error("Unable to update review due to an error.");
                    }

                    reviewUpdateSuccess = true;
                    document.getElementById('errorMessagesReview').textContent = '';
                    document.getElementById('resultMessagesReview').textContent = 'The review was updated successfully.';
                })
                .catch(error => {
                    document.getElementById('resultMessagesReview').textContent = '';
                    document.getElementById('errorMessagesReview').textContent = error.message;
                });
        });

        const closeReviewModalButton = document.getElementById('close-review-btn');
        closeReviewModalButton.addEventListener('click', function () {
            if (reviewUpdateSuccess) {
                window.location.href = '/users/reviews';
            }
        });
    }
});

// Update and Delete Rating Buttons
document.addEventListener('DOMContentLoaded', function () {
    const updateButtons = document.querySelectorAll('.update-rating-btn');
    updateButtons.forEach(button => {
        button.addEventListener('click', function () {
            const bookId = this.getAttribute('data-book-id');
            const rating = this.getAttribute('data-rating');
            const imgUrl = this.getAttribute('data-img-url');
            const img = document.getElementById('rating-modal-image');
            img.src = imgUrl;

            document.getElementById('bookRating').value = rating;

            const updateButton = document.getElementById('update-rating-btn');
            updateButton.setAttribute('data-book-id', bookId);

            document.getElementById('errorMessagesRating').textContent = '';
            document.getElementById('resultMessagesRating').textContent = '';
            document.getElementById('ratingModal').style.display = 'block';
            openBackdrop();
        });
    });


    const deleteButtons = document.querySelectorAll('.delete-rating-btn');
    deleteButtons.forEach(button => {
        button.addEventListener('click', function () {
            const bookId = this.getAttribute('data-book-id');
            const modalText = document.getElementById('commonModalText');
            const modalErrors = document.getElementById('commonModalErrors');
            const imgUrl = this.getAttribute('data-img-url');
            const img = document.getElementById('common-modal-image');
            img.src = imgUrl;

            fetch(`/books/${bookId}/rate`, {
                method: 'DELETE', headers: {
                    [csrfHeaderName]: csrfToken
                }
            })
                .then(response => {
                    return response.text().then(text => {
                        return {text: text, ok: response.ok};
                    });
                })
                .then(result => {
                    if (!result.ok) {
                        throw new Error(result.text);
                    }
                    updateResultCount();
                    document.getElementById(bookId).textContent = 'This rating was deleted.';
                    modalErrors.textContent = '';
                    modalText.textContent = result.text;
                    openCommonModal();
                })
                .catch(error => {
                    modalText.textContent = '';
                    modalErrors.textContent = error.message;
                    openCommonModal();
                });
        });
    });

    const updateButton = document.getElementById('update-rating-btn');
    if (updateButton) {
        let updateRatingSuccess = false;

        updateButton.addEventListener('click', function () {
            const bookId = this.getAttribute('data-book-id');
            const rating = document.getElementById('bookRating').value;

            if (rating === '') {
                document.getElementById('errorMessagesRating').textContent = 'Please select a rating.';
                return;
            }
            const ratingData = {rating};

            fetch(`/books/${bookId}/rate/`, {
                method: 'PUT', headers: {
                    'Content-Type': 'application/json', [csrfHeaderName]: csrfToken
                }, body: JSON.stringify(ratingData)
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error("Unable to update rating due to an error.");
                    }

                    updateRatingSuccess = true;
                    document.getElementById('errorMessagesRating').textContent = '';
                    document.getElementById('resultMessagesRating').textContent = 'The rating was updated successfully.';
                })
                .catch(error => {
                    document.getElementById('resultMessagesRating').textContent = '';
                    document.getElementById('errorMessagesRating').textContent = error.message;
                });
        });

        const closeRatingModalButton = document.getElementById('close-rating-btn');
        closeRatingModalButton.addEventListener('click', function () {
            if (updateRatingSuccess) {
                window.location.href = '/users/ratings';
            }
        });
    }
});


// Checkout Button
document.addEventListener('DOMContentLoaded', function () {
    const checkoutButton = document.querySelector('.checkout-btn');
    const commonModalCloseButton = document.getElementById('commonModalCloseButton');
    let checkoutSuccess = false;

    if (checkoutButton) {
        checkoutButton.addEventListener('click', function () {
            const modalText = document.getElementById('commonModalText');
            const modalErrors = document.getElementById('commonModalErrors');

            fetch(`/users/cart/checkout`, {
                method: 'POST', headers: {
                    [csrfHeaderName]: csrfToken
                }
            })
                .then(response => {
                    return response.text().then(text => {
                        return {text: text, ok: response.ok};
                    });
                })
                .then(result => {
                    if (!result.ok) {
                        throw new Error(result.text);
                    }
                    modalErrors.textContent = '';
                    modalText.textContent = result.text;
                    checkoutSuccess = true;
                    openCommonModal();
                })
                .catch(error => {
                    modalText.textContent = '';
                    modalErrors.textContent = error.message;
                    checkoutSuccess = false;
                    openCommonModal();
                });
        });

        commonModalCloseButton.addEventListener('click', function () {
            if (checkoutSuccess) {
                window.location.href = '/users/books';
            }
        });
    }
});


// Delete buttons for the cart and wishlist
document.addEventListener('DOMContentLoaded', function () {
    const modalText = document.getElementById('commonModalText');
    const modalErrors = document.getElementById('commonModalErrors');
    const deleteButtonsCart = document.querySelectorAll('.delete-from-cart-btn');
    const deleteButtonsList = document.querySelectorAll('.delete-from-list-btn');

    deleteButtonsCart.forEach(button => {
        button.addEventListener('click', function () {
            const bookId = this.getAttribute('data-book-id');
            const bookTitle = this.getAttribute('data-book-title');

            fetch(`/books/${bookId}/cart`, {
                method: 'DELETE', headers: {
                    [csrfHeaderName]: csrfToken
                }
            })
                .then(response => {
                    return response.text().then(text => {
                        return {text: text, ok: response.ok};
                    });
                })
                .then(result => {
                    if (!result.ok) {
                        throw new Error(result.text);
                    }

                    updateResultCount();
                    let numberOfBooksInCart = document.getElementById('numberOfBooksInCart');
                    if (parseInt(numberOfBooksInCart.textContent) >= 1) {
                        numberOfBooksInCart.textContent = (parseInt(numberOfBooksInCart.textContent) - 1).toString();
                    }
                    document.getElementById(bookId).textContent = `You have deleted from your Cart the eBook '${bookTitle}'.`;
                })
                .catch(error => {
                    modalText.textContent = '';
                    modalErrors.textContent = error.message;
                    openCommonModal();
                });
        });
    });

    deleteButtonsList.forEach(button => {
        button.addEventListener('click', function () {
            const bookId = this.getAttribute('data-book-id');
            const bookTitle = this.getAttribute('data-book-title');

            fetch(`/books/${bookId}/list`, {
                method: 'DELETE', headers: {
                    [csrfHeaderName]: csrfToken
                }
            })
                .then(response => {
                    return response.text().then(text => {
                        return {text: text, ok: response.ok};
                    });
                })
                .then(result => {
                    if (!result.ok) {
                        throw new Error(result.text);
                    }
                    updateResultCount();
                    document.getElementById(bookId).textContent = `You have deleted from your List the eBook '${bookTitle}'.`;
                })
                .catch(error => {
                    modalText.textContent = '';
                    modalErrors.textContent = error.message;
                    openCommonModal();
                });
        });
    });
});

function updateResultCount() {
    let startIndex = document.getElementById('startIndex');
    let endIndex = document.getElementById('endIndex');
    let numberOfItems = document.getElementById('numberOfItems');

    if (endIndex.textContent === '1') {
        startIndex.textContent = endIndex.textContent = numberOfItems.textContent = '0';
    } else if (parseInt(endIndex.textContent) >= 2) {
        endIndex.textContent = (parseInt(endIndex.textContent) - 1).toString();
        numberOfItems.textContent = (parseInt(numberOfItems.textContent) - 1).toString();
    }
}


// Admin Panel Buttons
document.addEventListener('DOMContentLoaded', function () {
    const modalText = document.getElementById('commonModalText');
    const modalErrors = document.getElementById('commonModalErrors');
    const addAdminRoleButtons = document.querySelectorAll('.add-admin-role-btn');
    const removeAdminRoleButtons = document.querySelectorAll('.remove-admin-role-btn');

    let rolesModification = false;

    addAdminRoleButtons.forEach(button => {
        button.addEventListener('click', function () {
            const userId = this.getAttribute('data-user-id');

            fetch(`/admin-panel/${userId}`, {
                method: 'POST', headers: {
                    [csrfHeaderName]: csrfToken
                }
            })
                .then(response => {
                    return response.text().then(text => {
                        return {text: text, ok: response.ok};
                    });
                })
                .then(result => {
                    if (!result.ok) {
                        throw new Error(result.text);
                    }
                    modalErrors.textContent = '';
                    modalText.textContent = 'You have added the Admin role to the user.';
                    openCommonModal();

                    const tr = document.getElementById(userId);
                    const userRolesTd = tr.querySelector('.user-roles');
                    const userButtonsTd = tr.querySelector('.user-buttons');

                    userRolesTd.textContent = 'User, Admin';
                    userButtonsTd.innerHTML = `
                        <div class="remove-admin-role-btn btn" data-user-id="${userId}">
                        Remove Admin Role</div>
                    `;
                    rolesModification = true;
                })
                .catch(error => {
                    modalText.textContent = '';
                    modalErrors.textContent = error.message;
                    openCommonModal();
                });
        });
    });

    removeAdminRoleButtons.forEach(button => {
        button.addEventListener('click', function () {
            const userId = this.getAttribute('data-user-id');

            fetch(`/admin-panel/${userId}`, {
                method: 'DELETE', headers: {
                    [csrfHeaderName]: csrfToken
                }
            })
                .then(response => {
                    return response.text().then(text => {
                        return {text: text, ok: response.ok};
                    });
                })
                .then(result => {
                    if (!result.ok) {
                        throw new Error(result.text);
                    }
                    modalErrors.textContent = '';
                    modalText.textContent = 'You have removed the Admin role from the user.';
                    openCommonModal();

                    const tr = document.getElementById(userId);
                    const userRolesTd = tr.querySelector('.user-roles');
                    const userButtonsTd = tr.querySelector('.user-buttons');

                    userRolesTd.textContent = 'User';
                    userButtonsTd.innerHTML = `
                        <div class="add-admin-role-btn btn" data-user-id="${userId}">
                        Add Admin Role</div>
                    `;
                    rolesModification = true;
                })
                .catch(error => {
                    modalText.textContent = '';
                    modalErrors.textContent = error.message;
                    openCommonModal();
                });
        });
    });

    if (addAdminRoleButtons.length > 0 || removeAdminRoleButtons.length > 0) {
        const closeButton = document.getElementById('commonModalCloseButton');
        closeButton.addEventListener('click', function () {
            window.location.reload();
        });
    }
});