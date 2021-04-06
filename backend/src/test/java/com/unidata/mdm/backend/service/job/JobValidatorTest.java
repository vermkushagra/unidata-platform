package com.unidata.mdm.backend.service.job;

import static com.unidata.mdm.backend.service.job.JobValidator.VIOLATION_ERROR;
import static com.unidata.mdm.backend.service.job.JobValidator.VIOLATION_JOB_CRON_EXPRESSION;
import static com.unidata.mdm.backend.service.job.JobValidator.VIOLATION_VALUE;
import static com.unidata.mdm.backend.service.job.JobValidator.VIOLATION_WARNING;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.spockframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.unidata.mdm.backend.api.rest.dto.Param;
import com.unidata.mdm.backend.common.exception.ExceptionId;
import com.unidata.mdm.backend.common.exception.JobException;

/**
 * @author Denis Kostovarov
 */
public class JobValidatorTest {

    private static final List<Pair<String, String>> PARAMS_WRONG_CRON_EXPRESSION =
            Collections.singletonList(new ImmutablePair(VIOLATION_JOB_CRON_EXPRESSION, VIOLATION_VALUE + VIOLATION_ERROR));
    private static final List<Pair<String, String>> PARAMS_SUSPICIOUS_EXPRESSION =
            Collections.singletonList(new ImmutablePair(VIOLATION_JOB_CRON_EXPRESSION, VIOLATION_VALUE + VIOLATION_WARNING));

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testValidCronExpression() {
        JobValidator.validateCronExpression("0 */30 * ? * *", false);
    }

    @Test
    public void testInvalidCronExpression() {
        thrown.expect(com.unidata.mdm.backend.common.exception.JobException.class);
        thrown.expect(new JobExceptionCodeMatcher(ExceptionId.EX_JOB_CRON_EXPRESSION, PARAMS_WRONG_CRON_EXPRESSION));
        JobValidator.validateCronExpression("* * * * * *", false);
    }

    @Test
    public void testSecondWarningCronExpression() {
        thrown.expect(com.unidata.mdm.backend.common.exception.JobException.class);
        thrown.expect(new JobExceptionCodeMatcher(ExceptionId.EX_JOB_CRON_SUSPICIOUS_SECOND, PARAMS_SUSPICIOUS_EXPRESSION));
        JobValidator.validateCronExpression("* 0 */3 ? * *", false);
    }

    @Test
    public void testMinuteWarningCronExpression() {
        thrown.expect(com.unidata.mdm.backend.common.exception.JobException.class);
        thrown.expect(new JobExceptionCodeMatcher(ExceptionId.EX_JOB_CRON_SUSPICIOUS_MINUTE, PARAMS_SUSPICIOUS_EXPRESSION));
        JobValidator.validateCronExpression("0 * */3 ? * *", false);
    }

    @Test
    public void testShortCyclesDayOfMonthWarningCronExpression() {
        thrown.expect(com.unidata.mdm.backend.common.exception.JobException.class);
        thrown.expect(new JobExceptionCodeMatcher(ExceptionId.EX_JOB_CRON_SUSPICIOUS_SHORT_CYCLES_DOM, PARAMS_SUSPICIOUS_EXPRESSION));
        JobValidator.validateCronExpression("0 0 3 */3 * ?", false);
    }

    @Test
    public void testSecondIgnoreWarningCronExpression() {
        JobValidator.validateCronExpression("* 0 */3 ? * *", true);
    }

    private static class JobExceptionCodeMatcher extends TypeSafeMatcher<JobException> {
        private ExceptionId code;
        private Map<String, String> paramsToMatch;

        JobExceptionCodeMatcher(final ExceptionId code) {
            Assert.notNull(code);
            this.code = code;
        }

        JobExceptionCodeMatcher(final ExceptionId code, final List<Pair<String, String>> params) {
            this(code);
            if (!CollectionUtils.isEmpty(params)) {
                paramsToMatch = new HashMap<>(params.size());
                for (final Pair<String, String> p : params) {
                    paramsToMatch.put(p.getLeft(), p.getRight());
                }
            }
        }

        @Override
        protected boolean matchesSafely(final JobException item) {
            boolean result = code.equals(item.getId());

            if (result && !CollectionUtils.isEmpty(paramsToMatch)) {
                result = false;
                final List<Pair<String, String>> itemParams = item.getParams();
                if (itemParams != null && paramsToMatch.size() == itemParams.size()) {
                    for (final Pair<String, String> p : itemParams) {
                        final String val = paramsToMatch.get(p.getKey());
                        if (!(result = Objects.equals(p.getValue(), val))) {
                            break;
                        }
                    }
                }
            }

            return result;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("expects code ").appendValue(code)
                    .appendText(" and params ").appendValue(paramsToMatch);
        }

        @Override
        protected void describeMismatchSafely(final JobException item, final Description mismatchDescription) {
            mismatchDescription.appendText("was ").appendValue(item.getId())
                    .appendText(" and params ").appendValue(paramsToMatch);
        }
    }
}
