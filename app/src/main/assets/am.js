gsap.to(".animation-element", {
    duration: 1,
    opacity: 1,
    y: 0,
    ease: "power4.out",
    delay: 0.5,
    onComplete: function() {
        gsap.to("#welcomeText", { duration: 1, scale: 1.2, ease: "elastic.out(1, 0.3)" });
    }
});