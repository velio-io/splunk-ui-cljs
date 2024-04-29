/// <reference types="cypress" />

describe('testing actions with map (predefined keys) control', () => {
    beforeEach(() => {
        cy.visit('http://localhost:6006/iframe.html?id=flow--simple-flow&viewMode=story')
            .wait(500)

        cy.get('.react-flow__node-action')
            .click()
            .find('[data-test="edit-action"]')
            .click()

        cy.get('.react-flow__node-action')
            .find('button[data-test=select]')
            .click()

        cy.wait(200)
    })

    afterEach(() => {
        cy.get('.react-flow__node-action')
            .find('button[type=submit]')
            .click()

        cy.get('.react-flow__node-action')
            .should('have.text', 'Test')
    })

    it('above-dt action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('above-dt')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="threshold"] input')
            .type('1.5')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="duration"] input')
            .type('3')
    })

    it('below-dt action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('below-dt')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="threshold"] input')
            .type('1.5')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="duration"] input')
            .type('3')
    })

    it('between-dt action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('between-dt')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="low"] input')
            .type('1.5')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="high"] input')
            .type('5')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="duration"] input')
            .type('3')
    })

    it('bottom action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('bottom')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="delay"] input')
            .type('1')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="duration"] input')
            .type('3')
    })

    it('changed action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('changed')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="field"] input')
            .type('first, [second, third]')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="init"] input')
            .type('test')
    })

    it('coalesce action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('coalesce')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="duration"] input')
            .type('4')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="fields"] input')
            .type('first, [second, third], fourth')
    })

    it('coll-bottom action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('coll-bottom')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="nb-events"] input')
            .type('4')
    })

    it('coll-top action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('coll-top')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="nb-events"] input')
            .type('4')
    })

    it('critical-dt action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('critical-dt')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="duration"] input')
            .type('3')
    })

    it('ewma-timeless action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('ewma-timeless')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="ratio"] input')
            .type('3')
    })

    it('extract action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('extract')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="key"] input')
            .type('test')
    })

    it('fixed-event-window action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('fixed-event-window')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="size"] input')
            .type('43')
    })

    it('fixed-time-window action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('fixed-time-window')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="duration"] input')
            .type('21')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="delay"] input')
            .type('3')
    })

    it('mean action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains(/^mean$/)
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="duration"] input')
            .type('21')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="delay"] input')
            .type('3')
    })

    it('moving-event-window action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('moving-event-window')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="size"] input')
            .type('21')
    })

    it('moving-time-window action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('moving-time-window')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="duration"] input')
            .type('256')
    })

    it('outside-dt action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('outside-dt')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="low"] input')
            .type('256')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="high"] input')
            .type('256')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="duration"] input')
            .type('256')
    })

    it('over action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('over')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="grater-than"] input')
            .type('123')
    })

    it('percentiles action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains(/^percentiles$/)
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="percentiles"] input')
            .type('0.95')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="duration"] input')
            .type('32')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="nb-significant-digits"] input')
            .type('3')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="delay"] input')
            .type('5')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="highest-trackable-value"] input')
            .type('23')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="lowest-discernible-value"] input')
            .type('13')
    })

    it('rate action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains(/^rate$/)
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="delay"] input')
            .type('1')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="duration"] input')
            .type('3')
    })

    it('reaper action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('reaper')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="interval"] input')
            .type('10')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="destination-stream"] input')
            .type('test-stream')
    })

    it('scale action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('scale')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="factor"] input')
            .type('10')
    })

    it('sformat action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('sformat')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="template"] input')
            .type('%s-foo-%s')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="target-field"] input')
            .type('test-field')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="fields"] input')
            .type('host, service')
    })

    it('ssort action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('ssort')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="duration"] input')
            .type('10')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="delay"] input')
            .type('21')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="field"] input')
            .type('host')
    })

    it('stable action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('stable')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="dt"] input')
            .type('10')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="field"] input')
            .type('host')
    })

    it('sum action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains(/^sum$/)
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="duration"] input')
            .type('10')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="delay"] input')
            .type('21')
    })

    it('throttle action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('throttle')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="duration"] input')
            .type('10')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="count"] input')
            .type('21')
    })

    it('top action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains(/^top$/)
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="duration"] input')
            .type('10')

        cy.get('.react-flow__node-action')
            .find('[data-test-field="delay"] input')
            .type('21')
    })

    it('under action test', () => {
        cy.get('[data-test=popover] [data-test=menu]')
            .find('button[data-test=option]')
            .contains('under')
            .click()

        cy.get('.react-flow__node-action')
            .find('[data-test-field="under"] input')
            .type('10')
    })
})
